package com.example.nesta.utils;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class JwtUtils {
    public static HashSet getRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        return new HashSet<>((Collection<String>) realmAccess.get("roles"));
    }
}
