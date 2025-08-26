package com.example.nesta.payment.api;

public record VerifyPayload(
        int merchantId,
        int posId,
        String sessionId,
        int amount,
        String currency,
        long orderId,
        String sign
) {}
