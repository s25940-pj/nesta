package com.example.nesta.dto.rentalinvoice;

public record RentalInvoiceCreateRequest(
        long rentalOfferId,
        String userId,
        int amountCents,
        String currency
) {}
