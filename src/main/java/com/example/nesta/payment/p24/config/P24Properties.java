package com.example.nesta.payment.p24.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "nesta.p24")
public record P24Properties(
        String baseUrl,
        String redirectHost,
        int merchantId,
        int posId,
        String apiKey,
        String crcKey,
        String returnUrl,
        String statusUrl
) {}
