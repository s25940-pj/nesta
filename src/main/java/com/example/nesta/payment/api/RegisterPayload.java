package com.example.nesta.payment.api;

public record RegisterPayload(
        int merchantId,
        int posId,
        String sessionId,
        int amount,
        String currency,
        String description,
        String email,
        String country,
        String language,
        String urlReturn,
        String urlStatus,
        String sign
) {}
