package com.example.nesta.service.rentalinvoice;

import com.example.nesta.dto.rentalinvoice.RentalInvoiceDto;
import com.example.nesta.dto.rentalinvoice.RentalInvoiceListItemDto;
import com.example.nesta.dto.rentalinvoice.RentalInvoiceQuery;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface RentalInvoiceService {
    RentalInvoiceDto create(RentalInvoiceDto req, String jwt);
    Page<RentalInvoiceListItemDto> listMine(RentalInvoiceQuery q, String userId);
    RentalInvoiceDto update(UUID id, RentalInvoiceDto update, String userId);
    void delete(UUID id, String userId);
}
