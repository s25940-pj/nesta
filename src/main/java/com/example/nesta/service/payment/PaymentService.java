package com.example.nesta.service.payment;

import com.example.nesta.payment.api.*;
import org.springframework.data.domain.Page;

public interface PaymentService {
    CheckoutResponse createCheckout(CheckoutRequest req, String userId);
    void handleP24Webhook(String rawBody, java.util.Map<String,String> headers);
    PaymentDto getBySession(String sessionId, String requesterId);
    Page<PaymentListItemDto> listMine(PaymentQuery q, String userId);
}
