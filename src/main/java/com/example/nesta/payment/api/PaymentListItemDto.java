package com.example.nesta.payment.api;

import java.time.Instant;
import java.util.UUID;

public record PaymentListItemDto(
        UUID id,
        String sessionId,
        String status,
        int amountCents,
        String currency,
        String method,
        UUID invoiceId,
        Instant createdAt,
        Instant paidAt
) {}
