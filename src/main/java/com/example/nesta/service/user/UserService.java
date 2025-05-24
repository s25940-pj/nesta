package com.example.nesta.service.user;

import com.example.nesta.dto.user.UserRegisterRequest;
import com.example.nesta.exception.user.UserCreationException;
import com.example.nesta.model.enums.UserRole;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Keycloak keycloak;

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

    public void assignRoleToUser(UserRole role, Jwt jwt) {
        String userId = jwt.getSubject();

        RoleRepresentation roleRepresentation = keycloak.realm(realm)
                .roles()
                .get(role.name())
                .toRepresentation();

        keycloak.realm(realm)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(List.of(roleRepresentation));
    }
}
