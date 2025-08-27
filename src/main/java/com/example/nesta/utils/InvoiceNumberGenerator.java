package com.example.nesta.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class InvoiceNumberGenerator {

    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);

    private static final String ALPHA = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();

    public String generate(Instant createdAtUtc, String userId) {
        String date = DATE.format(createdAtUtc);

        String userPrefix = (userId != null && userId.length() >= 4)
                ? userId.substring(0, 4).toUpperCase()
                : (userId != null ? userId.toUpperCase() : "USID");

        String rand = randomString(6);
        return "INV-" + date + "-" + userPrefix + "-" + rand;
    }

    private String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ALPHA.charAt(random.nextInt(ALPHA.length())));
        }
        return sb.toString();
    }
}
