package com.example.nesta.repository.rentalinvoice;

import com.example.nesta.dto.rentalinvoice.RentalInvoiceListItemDto;
import com.example.nesta.dto.rentalinvoice.RentalInvoiceQuery;
import org.springframework.data.domain.Page;

public interface RentalInvoiceRepositoryQuery {
    Page<RentalInvoiceListItemDto> searchMine(RentalInvoiceQuery q, String userId);
}