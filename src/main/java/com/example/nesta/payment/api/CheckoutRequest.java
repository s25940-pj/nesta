package com.example.nesta.payment.api;

import java.util.UUID;

public record CheckoutRequest(
        UUID invoiceId,
        String email,
        String description,
        String returnUrl
) {}
