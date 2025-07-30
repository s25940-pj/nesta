package com.example.nesta.service.user;

import com.example.nesta.dto.user.UserRegisterRequest;
import com.example.nesta.exception.user.UserCreationException;
import com.example.nesta.exception.user.UserLoginException;
import com.example.nesta.model.enums.UserRole;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
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
        validateRequest(request);

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

        String userId = getUserIdFromLocationHeaderUri(response.getLocation());

        try {
            assignRoleToUser(UserRole.valueOf(request.getRole()), userId);
        } catch (IllegalArgumentException e) {
            throw new UserCreationException("Failed to assign role in Keycloak. Invalid role: " + request.getRole());
        }
    }

    private void validateRequest(UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest.getUsername() == null || userRegisterRequest.getUsername().isEmpty()) {
            throw new UserCreationException("Username cannot be empty");
        }

        if (userRegisterRequest.getEmail() == null || userRegisterRequest.getEmail().isEmpty()) {
            throw new UserCreationException("Email cannot be empty");
        }

        if (userRegisterRequest.getPassword() == null || userRegisterRequest.getPassword().isEmpty()) {
            throw new UserCreationException("Password cannot be empty");
        }

        if (userRegisterRequest.getRole() == null || userRegisterRequest.getRole().isEmpty()) {
            throw new UserCreationException("Role cannot be empty");
        }
    }

    /**
     * Extracts the user ID from the Location URI returned by Keycloak after user creation.
     * <p>
     * The URI typically ends with "/users/{userId}", so this method isolates and returns
     * the {userId} segment from the path.
     *
     * @param uri the URI from the Location header of the Keycloak response
     * @return the extracted user ID
     */
    private String getUserIdFromLocationHeaderUri(URI uri) {
        // This regex captures the last segment of the URI path after the final slash.
        // Pattern explanation:
        // .*/        - matches everything up to the last '/'
        // ([^/]+)    - captures one or more characters that are not '/' (the userId)
        // $          - ensures it matches only at the end of the string
        // "$1"       - returns just the captured userId
        return uri.getPath().replaceAll(".*/([^/]+)$", "$1");
    }

    private void assignRoleToUser(UserRole role, String userId) {
        RoleRepresentation realmRole = keycloak.realm(realm)
                .roles()
                .get(role.name())
                .toRepresentation();

        keycloak.realm(realm)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(List.of(realmRole));
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
