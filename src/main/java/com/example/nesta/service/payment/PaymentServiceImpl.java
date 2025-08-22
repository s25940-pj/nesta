package com.example.nesta.service.payment;

import com.example.nesta.model.Payment;
import com.example.nesta.model.RentalInvoice;
import com.example.nesta.model.enums.PaymentStatus;
import com.example.nesta.model.enums.VerificationStatus;
import com.example.nesta.payment.api.*;
import com.example.nesta.payment.p24.client.P24Client;
import com.example.nesta.payment.p24.config.P24Properties;
import com.example.nesta.repository.payment.PaymentRepository;
import com.example.nesta.repository.rentalinvoice.RentalInvoiceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepo;
    private final RentalInvoiceRepository invoiceRepo;     // TODO
    private final P24Client p24;
    private final P24Properties props;
    private final ObjectMapper om;

    @Override
    @Transactional
    public CheckoutResponse createCheckout(CheckoutRequest req, String userId) {
        var invoice = invoiceRepo.findById(req.invoiceId())
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + req.invoiceId()));

        int amountCents = calculateAmountCents(invoice); // TODO
        String sessionId = "payment-" + UUID.randomUUID();

        // redirect if existing
        var existing = paymentRepo.findPendingByInvoiceId(invoice.getId());
        if (existing != null && existing.getP24Token() != null) {
            String cachedRedirect = props.redirectHost() + "/trnRequest/" + existing.getP24Token();
            return new CheckoutResponse(existing.getSessionId(), cachedRedirect);
        }

        var payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setUserId(userId);
        payment.setInvoiceId(invoice.getId());
        payment.setAmountCents(amountCents);
        payment.setCurrency("PLN");
        payment.setMethod("P24");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setVerificationStatus(VerificationStatus.UNVERIFIED);
        payment.setSessionId(sessionId);
        paymentRepo.save(payment);

        var registerReq = new P24Client.RegisterRequest(
                props.merchantId(),
                props.posId(),
                sessionId,
                amountCents,
                "PLN",
                req.description() != null ? req.description() : ("Opłata za fakturę " + invoice.getNumber()),
                req.email(),
                "PL",
                "pl",
                req.returnUrl() != null ? req.returnUrl() : props.returnUrl(),
                props.statusUrl()
        );

        var registerResp = p24.register(registerReq);

        payment.setP24Token(registerResp.token());
        payment.setP24OrderId(registerResp.orderId());
        paymentRepo.save(payment);

        String redirectUrl = props.redirectHost() + "/trnRequest/" + registerResp.token();
        return new CheckoutResponse(sessionId, redirectUrl);
    }

    @Override
    @Transactional
    public void handleP24Webhook(String rawBody, Map<String, String> headers) {
        JsonNode node;
        try { node = om.readTree(rawBody); }
        catch (Exception e) { return; } // 200 if ok, 5xx if error

        JsonNode data = node.get("data");
        if (data == null) return;

        String sessionId = text(data, "sessionId");
        Integer amount = intOrNull(data, "amount");
        String currency = text(data, "currency");

        if (sessionId == null || amount == null || currency == null) return;

        var payment = paymentRepo.findBySessionId(sessionId);
        if (payment == null) return;

        // if paid already return
        if (payment.getStatus() == PaymentStatus.PAID) return;

        // simple validation, if currency wrong, save notification but don't confirm
        if (payment.getAmountCents() != amount || !"PLN".equals(currency)) {
            payment.setRawNotification(truncate(rawBody, 4000));
            paymentRepo.save(payment);
            return;
        }

        // verify
        var verifyReq = new P24Client.VerifyRequest(
                props.merchantId(),
                props.posId(),
                sessionId,
                amount,
                currency
        );
        var verify = p24.verify(verifyReq);

        if (verify.status()) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setVerificationStatus(VerificationStatus.VERIFIED);
            payment.setPaidAt(Instant.now());
            payment.setRawNotification(truncate(rawBody, 4000));
            paymentRepo.save(payment);

            // TODO
            invoiceRepo.findById(payment.getInvoiceId()).ifPresent(inv -> {
                inv.setPaid(true);
                inv.setPaidAt(payment.getPaidAt());
                inv.setPaymentId(payment.getId());
                invoiceRepo.save(inv);
            });
        } else {
            // save notification if failed, TODO retry payment?
            payment.setStatus(PaymentStatus.FAILED);
            payment.setRawNotification(truncate(rawBody, 4000));
            paymentRepo.save(payment);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getBySession(String sessionId, String requesterId) {
        var p = paymentRepo.findBySessionId(sessionId);
        if (p == null) throw new IllegalArgumentException("Payment not found");
        if (!p.getUserId().equals(requesterId) && !isAdmin()) {
            throw new SecurityException("Forbidden");
        }
        return new PaymentDto(
                p.getSessionId(),
                p.getStatus().name(),
                p.getAmountCents(),
                p.getCurrency(),
                p.getMethod(),
                p.getInvoiceId() != null ? p.getInvoiceId().toString() : null,
                p.getPaidAt() != null ? p.getPaidAt().toString() : null
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentListItemDto> listMine(PaymentQuery q, String userId) {
        return paymentRepo.searchMine(q, userId);
    }

    private int calculateAmountCents(RentalInvoice inv) {
        // TODO, currency?
        return inv.getAmountCents();
    }

    private String text(JsonNode n, String field) {
        return n.hasNonNull(field) ? n.get(field).asText() : null;
    }

    private Integer intOrNull(JsonNode n, String field) {
        return n.hasNonNull(field) ? n.get(field).asInt() : null;
    }

    private String truncate(String s, int max) {
        return (s == null || s.length() <= max) ? s : s.substring(0, max);
    }

    // TODO: get this somehow from keycloak?
    private boolean isAdmin() {
        return false;
    }
}
