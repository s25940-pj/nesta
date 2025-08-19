package com.example.nesta.controller.rentalinvoice;

import com.example.nesta.dto.rentalinvoice.RentalInvoiceCreateRequest;
import com.example.nesta.model.RentalInvoice;
import com.example.nesta.repository.rentalinvoice.RentalInvoiceRepository;
import com.example.nesta.repository.rentaloffer.RentalOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class RentalInvoiceController {
    private final RentalInvoiceRepository invoiceRepo;
    private final RentalOfferRepository offerRepo;

    @PostMapping("/create")
    public RentalInvoice create(@RequestBody RentalInvoiceCreateRequest req) {
        var offer = offerRepo.findById(req.rentalOfferId())
                .orElseThrow(() -> new IllegalArgumentException("Offer with id " + req.rentalOfferId() + " does not exist"));

        var inv = new RentalInvoice();
        inv.setRentalOffer(offer);
        inv.setUserId(req.userId());
        inv.setAmountCents(req.amountCents());
        inv.setCurrency(req.currency() != null ? req.currency() : "PLN");
        inv.setPaid(false);
        inv.setNumber(null);

        return invoiceRepo.save(inv);
    }
}