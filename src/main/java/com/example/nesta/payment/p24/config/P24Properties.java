package com.example.nesta.payment.p24.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nesta.p24")
public record P24Properties(
        String baseUrl,
        String redirectHost,
        String merchantId,
        String posId,
        String crcKey,
        String returnUrl,
        String statusUrl
) {}
