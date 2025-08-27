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
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepo;
    private final RentalInvoiceRepository invoiceRepo;
    private final P24Client p24;
    private final P24Properties props;

    @Override
    @Transactional
    public CheckoutResponse createCheckout(CheckoutRequest req, String userId) {
        var invoice = invoiceRepo.findById(req.invoiceId())
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + req.invoiceId()));

        int amountCents = calculateAmountCents(invoice);
        String sessionId = "payment-" + invoice.getId();

        // redirect if existing
        var existing = paymentRepo.findPendingByInvoiceId(invoice.getId());
        log.debug("==== Redirecting because payment already exists ====");
        if (existing != null && existing.getP24Token() != null) {
            String cachedRedirect = props.redirectHost() + "/trnRequest/" + existing.getP24Token();
            return new CheckoutResponse(existing.getSessionId(), cachedRedirect);
        }

        var payment = new Payment();
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

        P24Client.RegisterResponse registerResp;
        try {
            log.info("P24 props sanity: merchantId={}, posId={}, crc_len={}",
                    props.merchantId(), props.posId(),
                    props.crcKey() == null ? "null" : props.crcKey().length());

            if (props.merchantId() == 0 || props.posId() == 0 || props.crcKey() == null || props.crcKey().isBlank()) {
                throw new IllegalStateException("P24 config missing: merchantId/posId/crc must be set");
            }
            registerResp = p24.register(registerReq);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        payment.setP24Token(registerResp.token());
        paymentRepo.save(payment);

        String redirectUrl = props.redirectHost() + "/trnRequest/" + registerResp.token();
        return new CheckoutResponse(sessionId, redirectUrl);
    }

    @Override
    @Transactional
    public void handleP24Webhook(WebhookRequest req) {
        log.debug("SERVICE INIT HANDLE WEEBHOOK");
        log.debug("DATA IS: " + req.toString());

        String sessionId = req.sessionId();
        Integer amount = req.amount();
        String currency = req.currency();

        if (sessionId == null || amount == null || currency == null) return;

        var payment = paymentRepo.findBySessionId(sessionId);
        if (payment == null) return;

        // if paid already return
        if (payment.getStatus() == PaymentStatus.PAID) return;

        // simple validation, if currency or amount wrong, save notification but don't confirm
        if (payment.getAmountCents() != amount || !"PLN".equals(currency)) {
            payment.setRawNotification("Currency/amount mismatch payment attempt :"
                    + amount + "/" + currency);
            paymentRepo.save(payment);
            return;
        }

        // verify
        var verifyReq = new P24Client.VerifyRequest(
                req.merchantId(),
                req.posId(),
                sessionId,
                amount,
                currency,
                req.orderId()
        );

        P24Client.VerifyResponse verify;
        try {
            log.debug("CALLING P24 CLIENT WITH VERIFY REQUEST");
            verify = p24.verify(verifyReq);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (verify.status().equals("success")) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setVerificationStatus(VerificationStatus.VERIFIED);
            payment.setPaidAt(Instant.now());
            payment.setRawNotification(truncate(req.toString(), 4000));
            paymentRepo.save(payment);

            invoiceRepo.findById(payment.getInvoiceId()).ifPresent(inv -> {
                inv.setPaid(true);
                inv.setPaidAt(payment.getPaidAt());
                inv.setPaymentId(payment.getId());
                invoiceRepo.save(inv);
            });
        } else {
            // save notification if failed, TODO retry payment?
            payment.setStatus(PaymentStatus.FAILED);
            payment.setRawNotification(truncate(req.toString(), 4000));
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

    private String truncate(String s, int max) {
        return (s == null || s.length() <= max) ? s : s.substring(0, max);
    }

    // TODO: get this somehow from keycloak?
    private boolean isAdmin() {
        return false;
    }
}
