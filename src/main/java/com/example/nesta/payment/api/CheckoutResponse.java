package com.example.nesta.payment.api;

public record CheckoutResponse(
        String sessionId,
        String redirectUrl
) {}
