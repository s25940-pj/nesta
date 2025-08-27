package com.example.nesta.dto.rentalinvoice;

import java.time.Instant;
import java.util.UUID;

public record RentalInvoiceDto(
        UUID id,
        Long rentalOfferId,
        String issuerId,
        String receiverId,
        Integer amountCents,
        String currency,
        Boolean paid,
        String number,
        Instant createdAt,
        Instant updatedAt,
        Instant paidAt
) {}
