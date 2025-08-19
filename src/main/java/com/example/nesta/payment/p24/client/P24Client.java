package com.example.nesta.payment.p24.client;

import com.example.nesta.payment.p24.config.P24Properties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Component
public class P24Client {
    private final RestTemplate restTemplate;
    private final ObjectMapper om;
    private final P24Properties props;

    public P24Client(P24Properties props, ObjectMapper om) {
        this.props = props;
        this.om = om;
        this.restTemplate = new RestTemplate();
    }

    public RegisterResponse register(RegisterRequest req) {
        String payload = toJsonUnchecked(req);
        String sign = hmacSha384(payload, props.crcKey());
        RegisterEnvelope body = new RegisterEnvelope(req, sign);

        RegisterEnvelopeResponse resp = restTemplate.postForObject(
                props.baseUrl() + "/transaction/register",
                body,
                RegisterEnvelopeResponse.class
        );
        if (resp == null || resp.data() == null || resp.data().token() == null) {
            throw new IllegalStateException("P24 register failed: " + (resp == null ? "null" : resp.error()));
        }
        return resp.data();
    }

    public VerifyResponse verify(VerifyRequest req) {
        String payload = toJsonUnchecked(req);
        String sign = hmacSha384(payload, props.crcKey());
        VerifyEnvelope body = new VerifyEnvelope(req, sign);

        VerifyEnvelopeResponse resp = restTemplate.exchange(
                props.baseUrl() + "/transaction/verify",
                HttpMethod.PUT,
                new HttpEntity<>(body),
                VerifyEnvelopeResponse.class
        ).getBody();

        if (resp == null || resp.data() == null) {
            throw new IllegalStateException("P24 verify failed: " + (resp == null ? "null" : resp.error()));
        }
        return resp.data();
    }

    private String toJsonUnchecked(Object o) {
        try { return om.writeValueAsString(o); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    private String hmacSha384(String payloadJson, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA384");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA384"));
            byte[] raw = mac.doFinal(payloadJson.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(raw.length * 2);
            for (byte b : raw) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC(SHA-384) failed", e);
        }
    }

    // ====== TODO: request/response models ======

    public record RegisterRequest(
            String merchantId,
            String posId,
            String sessionId,
            int amount,
            String currency,
            String description,
            String email,
            String country,
            String language,
            String urlReturn,
            String urlStatus
    ) {}

    public record RegisterResponse(String token, Long orderId) {}

    public record RegisterEnvelope(RegisterRequest data, String sign) {}
    public record RegisterEnvelopeResponse(RegisterResponse data, String error) {}

    public record VerifyRequest(
            String merchantId,
            String posId,
            String sessionId,
            int amount,
            String currency
    ) {}

    public record VerifyResponse(boolean status) {}

    public record VerifyEnvelope(VerifyRequest data, String sign) {}
    public record VerifyEnvelopeResponse(VerifyResponse data, String error) {}
}

