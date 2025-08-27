package com.example.nesta.service.rentalinvoice;

import com.example.nesta.dto.rentalinvoice.RentalInvoiceDto;
import com.example.nesta.dto.rentalinvoice.RentalInvoiceListItemDto;
import com.example.nesta.dto.rentalinvoice.RentalInvoiceQuery;
import com.example.nesta.model.RentalInvoice;
import com.example.nesta.model.RentalOffer;
import com.example.nesta.repository.rentalinvoice.RentalInvoiceRepository;
import com.example.nesta.repository.rentaloffer.RentalOfferRepository;
import com.example.nesta.utils.InvoiceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalInvoiceServiceImpl implements RentalInvoiceService {

    private final RentalInvoiceRepository invoiceRepo;
    private final RentalOfferRepository rentalOfferRepo;
    private final InvoiceNumberGenerator numberGenerator;

    @Override
    @Transactional
    public RentalInvoiceDto create(RentalInvoiceDto req, String userId) {
        RentalOffer offer = rentalOfferRepo.findById(req.rentalOfferId())
                .orElseThrow(() -> new IllegalArgumentException("Offer with id %s does not exist".formatted(req.rentalOfferId())));

        RentalInvoice inv = new RentalInvoice();
        inv.setRentalOffer(offer);
        inv.setIssuerId(userId);
        inv.setReceiverId(req.receiverId()); // if rentalOffer will hold the rentier info, change it to load from there
        inv.setAmountCents(req.amountCents());
        inv.setCurrency(req.currency() != null ? req.currency() : "PLN");
        inv.setPaid(false);

        int attempts = 0;
        while (true) {
            attempts++;
            inv.setNumber(numberGenerator.generate(Instant.now(), userId));
            try {
                RentalInvoice saved = invoiceRepo.save(inv);
                return toDto(saved);
            } catch (DataIntegrityViolationException e) {
                if (attempts >= 5) throw e;
            }
        }
    }

    @Override
    public RentalInvoiceDto update(UUID id, RentalInvoiceDto invoiceDto, String userId) {
        var inv = invoiceRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found"));
        log.debug("Service: found candidate for update, DTO is: ");
        log.debug(invoiceDto.toString());
        if (invoiceDto.amountCents() != null) {
            if (invoiceDto.amountCents() <= 0) throw new IllegalArgumentException("amountCents must be > 0");
            log.debug("Recognized amount from DTO, updating");
            inv.setAmountCents(invoiceDto.amountCents());
        }

        if (invoiceDto.currency() != null && !invoiceDto.currency().isBlank()) {
            inv.setCurrency(invoiceDto.currency().trim().toUpperCase());
        }

        if (invoiceDto.paid() != null) {
            boolean newPaid = invoiceDto.paid();
            inv.setPaid(newPaid);

            if (newPaid) {
                inv.setPaidAt(invoiceDto.paidAt() != null ? invoiceDto.paidAt() : Instant.now());
            } else {
                inv.setPaidAt(null);
            }
        } else if (invoiceDto.paidAt() != null) {
            if (inv.isPaid()) {
                inv.setPaidAt(invoiceDto.paidAt());
            } else {
                throw new IllegalArgumentException("Cannot set paidAt when paid=false");
            }
        }
        invoiceRepo.save(inv);
        return toDto(inv);
    }

    @Override
    public void delete(UUID id, String userId) {
        var inv = invoiceRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found"));

        if (inv.isPaid()) {
            throw new IllegalStateException("Paid invoices cannot be deleted");
        }
        invoiceRepo.delete(inv);
    }

    @Override
    public Page<RentalInvoiceListItemDto> listMine(RentalInvoiceQuery q, String userId) {
        return invoiceRepo.searchMine(q, userId);
    }

    private static RentalInvoiceDto toDto(RentalInvoice e) {
        return new RentalInvoiceDto(
                e.getId(),
                e.getRentalOffer() != null ? e.getRentalOffer().getId() : null,
                e.getIssuerId(),
                e.getReceiverId(),
                e.getAmountCents(),
                e.getCurrency(),
                e.isPaid(),
                e.getNumber(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getPaidAt()
        );
    }
}
