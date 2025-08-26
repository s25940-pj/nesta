package com.example.nesta.controller.payment;

import com.example.nesta.payment.api.*;
import com.example.nesta.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest req,
                                     @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return paymentService.createCheckout(req, userId);
    }

    // consider improvement?
    @PostMapping("/webhook/p24")
    public ResponseEntity<String> webhook(@RequestBody WebhookRequest req) throws IOException {
        log.debug("======= WEBHOOK INITIALIZED =======");
        log.debug(req.toString());
        paymentService.handleP24Webhook(req);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/{sessionId}")
    public PaymentDto bySession(@PathVariable String sessionId,
                                @AuthenticationPrincipal Jwt jwt) {
        return paymentService.getBySession(sessionId, jwt.getSubject());
    }

    @GetMapping("/list")
    public Page<PaymentListItemDto> listMine(@ModelAttribute PaymentQuery q, @AuthenticationPrincipal Jwt jwt) {
        return paymentService.listMine(q, jwt.getSubject());
    }
}

