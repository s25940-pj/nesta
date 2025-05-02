package com.example.nesta.service.user;

import com.example.nesta.dto.RegisterRequest;
import com.example.nesta.exception.user.UserCreationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Keycloak keycloak;

    public void registerUser(RegisterRequest request) {
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

        String realm = "nesta-realm";
        Response response = keycloak.realm(realm).users().create(user);

        if (response.getStatus() != 201) {
            throw new UserCreationException("Failed to create user in Keycloak. Status code: " + response.getStatus());
        }

    }
}
