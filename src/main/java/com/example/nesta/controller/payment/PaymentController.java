package com.example.nesta.controller.payment;

import com.example.nesta.payment.api.*;
import com.example.nesta.service.payment.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<Void> webhook(HttpServletRequest request) throws IOException {
        String raw = request.getReader().lines().collect(Collectors.joining("\n"));
        Map<String, String> headers = Collections.list(request.getHeaderNames())
                .stream().collect(Collectors.toMap(h -> h, request::getHeader));
        paymentService.handleP24Webhook(raw, headers);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{sessionId}")
    public PaymentDto bySession(@PathVariable String sessionId,
                                @AuthenticationPrincipal Jwt jwt) {
        return paymentService.getBySession(sessionId, jwt.getSubject());
    }

    @GetMapping
    public Page<PaymentListItemDto> listMine(PaymentQuery q, @AuthenticationPrincipal Jwt jwt) {
        return paymentService.listMine(q, jwt.getSubject());
    }
}

