package com.example.nesta.payment.api;

import java.time.Instant;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

public record PaymentQuery(
        String status,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant from,                   // from createdAt
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant to,                     // after createdAt
        UUID invoiceId,
        Integer page,                   // default 0
        Integer size                    // default 20
) {}
