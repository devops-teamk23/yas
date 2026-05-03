package com.yas.inventory.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.inventory.constants.ApiConstant;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthenticationUtilsTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testExtractUserId_whenAnonymous_thenThrowAccessDenied() {
        AnonymousAuthenticationToken anonymous = new AnonymousAuthenticationToken(
            "key",
            "anonymousUser",
            List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        );
        setAuthentication(anonymous);

        AccessDeniedException thrown = assertThrows(AccessDeniedException.class, AuthenticationUtils::extractUserId);

        assertEquals(ApiConstant.ACCESS_DENIED, thrown.getMessage());
    }

    @Test
    void testExtractUserId_whenJwt_thenReturnSubject() {
        Jwt jwt = new Jwt(
            "token-value",
            Instant.now(),
            Instant.now().plusSeconds(300),
            Map.of("alg", "none"),
            Map.of("sub", "user-1")
        );
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
        setAuthentication(authentication);

        String userId = AuthenticationUtils.extractUserId();

        assertEquals("user-1", userId);
    }

    @Test
    void testExtractJwt_returnsTokenValue() {
        Jwt jwt = new Jwt(
            "jwt-token",
            Instant.now(),
            Instant.now().plusSeconds(300),
            Map.of("alg", "none"),
            Map.of("sub", "user-2")
        );
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
        setAuthentication(authentication);

        String token = AuthenticationUtils.extractJwt();

        assertEquals("jwt-token", token);
    }

    private void setAuthentication(Authentication authentication) {
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
    }
}
