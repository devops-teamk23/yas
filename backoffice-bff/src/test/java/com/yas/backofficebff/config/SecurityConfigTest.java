package com.yas.backofficebff.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

class SecurityConfigTest {

    private static final String ADMIN = "ADMIN";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Test
    void shouldGenerateAuthoritiesFromClaim() {
        SecurityConfig securityConfig = new SecurityConfig(mock(ReactiveClientRegistrationRepository.class));

        var authorities = securityConfig.generateAuthoritiesFromClaim(List.of(ADMIN, "STAFF"));

        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> ROLE_ADMIN.equals(a.getAuthority())));
        assertTrue(authorities.stream().anyMatch(a -> "ROLE_STAFF".equals(a.getAuthority())));
    }

    @Test
    void shouldMapAuthoritiesFromOauth2Claim() {
        SecurityConfig securityConfig = new SecurityConfig(mock(ReactiveClientRegistrationRepository.class));

        Map<String, Object> attributes = Map.of(
            "realm_access", Map.of("roles", List.of(ADMIN, "MANAGER"))
        );
        OAuth2UserAuthority authority = new OAuth2UserAuthority(attributes);

        var mappedAuthorities = securityConfig
            .userAuthoritiesMapperForKeycloak()
            .mapAuthorities(Set.of(authority));

        assertEquals(2, mappedAuthorities.size());
        assertTrue(mappedAuthorities.stream().anyMatch(a -> ROLE_ADMIN.equals(a.getAuthority())));
        assertTrue(mappedAuthorities.stream().anyMatch(a -> "ROLE_MANAGER".equals(a.getAuthority())));
    }

    @Test
    void shouldMapAuthoritiesFromOidcClaim() {
        SecurityConfig securityConfig = new SecurityConfig(mock(ReactiveClientRegistrationRepository.class));

        OidcIdToken idToken = new OidcIdToken(
            "token-value",
            Instant.now(),
            Instant.now().plusSeconds(300),
            Map.of("sub", "user-1")
        );
        OidcUserInfo userInfo = new OidcUserInfo(Map.of(
            "realm_access", Map.of("roles", List.of(ADMIN))
        ));
        OidcUserAuthority authority = new OidcUserAuthority(idToken, userInfo);

        var mappedAuthorities = securityConfig
            .userAuthoritiesMapperForKeycloak()
            .mapAuthorities(Set.of(authority));

        assertEquals(1, mappedAuthorities.size());
        assertTrue(mappedAuthorities.stream().anyMatch(a -> ROLE_ADMIN.equals(a.getAuthority())));
    }

    @Test
    void shouldReturnEmptyAuthoritiesWhenRealmAccessMissing() {
        SecurityConfig securityConfig = new SecurityConfig(mock(ReactiveClientRegistrationRepository.class));

        OAuth2UserAuthority authority = new OAuth2UserAuthority(Map.of("sub", "user-1"));

        var mappedAuthorities = securityConfig
            .userAuthoritiesMapperForKeycloak()
            .mapAuthorities(Set.of(authority));

        assertTrue(mappedAuthorities.isEmpty());
    }
}
