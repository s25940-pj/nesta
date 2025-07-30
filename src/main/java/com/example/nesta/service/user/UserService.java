package com.example.nesta.service.user;

import com.example.nesta.dto.user.UserRegisterRequest;
import com.example.nesta.exception.user.UserCreationException;
import com.example.nesta.exception.user.UserLoginException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Keycloak keycloak;

    @Value("${keycloak.token-url}")
    private String tokenUrl;

    private final String realm = "nesta-realm";

    public void registerUser(UserRegisterRequest request) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);

        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setCredentials(List.of(credential));

        Response response = keycloak.realm(realm).users().create(user);

        if (response.getStatus() != 201) {
            throw new UserCreationException("Failed to create user in Keycloak. Status code: " + response.getStatus());
        }
    }

    public ResponseEntity<String> loginUser(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "password");
        body.put("client_id", "nesta-backend");
        body.put("client_secret", "12345");
        body.put("username", username);
        body.put("password", password);

        HttpEntity<String> request = new HttpEntity<>(buildUrlEncodedBody(body), headers);

        try {
            return restTemplate.postForEntity(tokenUrl, request, String.class);
        } catch (Exception e) {
            throw new UserLoginException("Login failed: " + e.getMessage());
        }
    }

    private String buildUrlEncodedBody(Map<String, String> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return sb.toString().replaceAll("&$", "");
    }
}
