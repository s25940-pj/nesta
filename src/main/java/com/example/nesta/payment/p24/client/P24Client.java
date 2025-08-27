package com.example.nesta.payment.p24.client;

import com.example.nesta.payment.api.RegisterPayload;
import com.example.nesta.payment.api.VerifyPayload;
import com.example.nesta.payment.p24.config.P24Properties;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


@Slf4j
@Component
public class P24Client {
    private final RestTemplate restTemplate;
    private final P24Properties props;

    public P24Client(P24Properties props) {
        this.props = props;
        this.restTemplate = new RestTemplate();
    }

    public RegisterResponse register(RegisterRequest req) throws Exception {
        String toSign = createSignJson(req.sessionId, "merchantId", req.merchantId, req.amount,req.currency, props.crcKey());
        log.debug("=========STRING IDACY DO SHA=========");
        log.debug(toSign);
        String sign = sha384Hex(toSign);
        log.debug(sign);
        RegisterPayload payload = new RegisterPayload(
                req.merchantId,
                req.posId,
                req.sessionId,
                req.amount,
                req.currency,
                req.description,
                req.email,
                req.country,
                req.language,
                req.urlReturn,
                req.urlStatus,
                sign
        );
        log.debug("+++++++ REGISTER PAYLOAD +++++++");
        log.debug(payload.toString());

        HttpEntity<RegisterPayload> entity = new HttpEntity<>(payload, authHeaders());
        log.debug("=== Sending register request to P24 ===");
        RegisterEnvelopeResponse resp = restTemplate.postForObject(
                props.baseUrl() + "/transaction/register",
                entity,
                RegisterEnvelopeResponse.class
        );
        if (resp != null) {
            log.debug("REGISTER RESPONSE: data: " + resp.data);
        }
        if (resp == null || resp.data() == null || resp.data().token() == null) {
            throw new IllegalStateException("P24 register failed: " + (resp == null ? "null" : resp.error()));
        }
        return resp.data();
    }

    public VerifyResponse verify(VerifyRequest req) throws Exception {
        String toSign = createSignJson(req.sessionId, "orderId", req.orderId, req.amount,req.currency, props.crcKey());
        String sign = sha384Hex(toSign);
        VerifyPayload payload = new VerifyPayload(
                req.merchantId,
                req.posId,
                req.sessionId,
                req.amount,
                req.currency,
                req.orderId,
                sign
        );
        log.debug("+++++++ VERIFY PAYLOAD +++++++");
        log.debug(payload.toString());


        HttpEntity<VerifyPayload> entity = new HttpEntity<>(payload, authHeaders());
        log.debug("=== Sending verify request to P24 ===");
        VerifyEnvelopeResponse resp = restTemplate.exchange(
                props.baseUrl() + "/transaction/verify",
                HttpMethod.PUT,
                entity,
                VerifyEnvelopeResponse.class
        ).getBody();

        if (resp == null || resp.data() == null) {
            log.debug("VERIFY RESPONSE: data: " + resp.data);
            throw new IllegalStateException("P24 verify failed: " + (resp == null ? "null" : resp.error()));
        }
        return resp.data();
    }

    private String sha384Hex(String data) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-384").digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-384 failed", e);
        }
    }

    public record RegisterRequest(
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
            String urlStatus
    ) {}

    public record RegisterResponse(String token) {}
    public record RegisterEnvelopeResponse(RegisterResponse data, String error) {}

    public record VerifyRequest(
            int merchantId,
            int posId,
            String sessionId,
            int amount,
            String currency,
            long orderId
    ) {}

    public record VerifyResponse(String status) {}
    public record VerifyEnvelopeResponse(VerifyResponse data, String error) {}

    private HttpHeaders authHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.setBasicAuth(String.valueOf(props.merchantId()), props.apiKey());
        return h;
    }

    private static String createSignJson(String sessionId, String idKey, long idValue, int amount, String currency, String crc) throws Exception {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("sessionId", sessionId);
        n.put(idKey, idValue);
        n.put("amount", amount);
        n.put("currency", currency);
        n.put("crc", crc);
        return MAPPER.writeValueAsString(n);
    }

    // make sure deserialization doesn't mess up json request properties order, cause it will break sign
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);

}



