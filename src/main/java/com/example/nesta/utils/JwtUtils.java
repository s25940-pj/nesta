package com.example.nesta.utils;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class JwtUtils {
    public static HashSet getRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        return new HashSet<>((Collection<String>) realmAccess.get("roles"));
    }

    public static void requireOwner(Jwt jwt, String id) {
        var ownerId = jwt.getSubject();
        if (!id.equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This user is not owner of this resource");
        }
    }
}
