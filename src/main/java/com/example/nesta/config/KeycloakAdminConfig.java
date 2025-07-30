package com.example.nesta.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {
    @Value("${keycloak.base-url}")
    private String baseUrl;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(baseUrl)
                .realm("master")
                .username("admin")
                .password("admin")
                .clientId("admin-cli")
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }
}
