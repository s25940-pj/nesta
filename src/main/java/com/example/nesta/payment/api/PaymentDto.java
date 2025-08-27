package com.example.nesta.payment.api;

public record PaymentDto(
        String sessionId,
        String status,
        int amountCents,
        String currency,
        String method,
        String invoiceId,
        String paidAt
) {}
