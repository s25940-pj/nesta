package com.example.nesta.dto.rentalinvoice;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

public record RentalInvoiceQuery(
        String number,
        Boolean paid,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant from,                               // createdAt >= from
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant to,                                 // createdAt <= to
        Long offerId,
        Integer page,                               // default 0
        Integer size,                               // default 20
        String sortBy,                              // createdAt|paidAt|amountCents|number
        String sortDir
) {
}
