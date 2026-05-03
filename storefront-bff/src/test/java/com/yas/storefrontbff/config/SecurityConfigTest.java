package com.yas.storefrontbff.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig(
            org.mockito.Mockito.mock(ReactiveClientRegistrationRepository.class)
    );

    @Test
    void shouldGenerateRoleAuthoritiesFromClaim() {
        var authorities = securityConfig.generateAuthoritiesFromClaim(List.of("customer", "admin"));

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_customer", "ROLE_admin");
    }

    @Test
    void shouldMapOauth2RealmRolesToAuthorities() {
        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        OAuth2UserAuthority authority = new OAuth2UserAuthority(Map.of(
                "realm_access", Map.of("roles", List.of("buyer", "seller"))
        ));

        var mappedAuthorities = mapper.mapAuthorities(List.of(authority));

        assertThat(mappedAuthorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_buyer", "ROLE_seller");
    }

    @Test
    void shouldMapOidcRealmRolesToAuthorities() {
        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        OidcIdToken idToken = new OidcIdToken(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(60),
                Map.of("sub", "customer")
        );
        OidcUserInfo userInfo = new OidcUserInfo(Map.of(
                "realm_access", Map.of("roles", List.of("vip"))
        ));
        OidcUserAuthority authority = new OidcUserAuthority(idToken, userInfo);

        var mappedAuthorities = mapper.mapAuthorities(List.of(authority));

        assertThat(mappedAuthorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_vip");
    }
}
