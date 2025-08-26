package com.example.nesta.dto.rentalinvoice;

import java.time.Instant;
import java.util.UUID;

public record RentalInvoiceListItemDto(
        UUID id,
        String number,
        Integer amountCents,
        String currency,
        Boolean paid,
        Instant createdAt,
        Instant paidAt,
        Long rentalOfferId
) {
}
