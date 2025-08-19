package com.example.nesta.payment.api;

public record WebhookRequest(
    int merchantId,
    int posId,
    String sessionId,
    int amount,
    int originAmount,
    String currency,
    long orderId,
    Integer methodId,
    String statement,
    String sign
){}
