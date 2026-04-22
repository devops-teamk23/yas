package com.yas.storefrontbff.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;

class AuthenticationControllerTest {

    private final AuthenticationController controller = new AuthenticationController();

    @Test
    void shouldReturnAnonymousAuthenticationInfoWhenPrincipalIsMissing() {
        var response = controller.user(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isAuthenticated()).isFalse();
        assertThat(response.getBody().authenticatedUser()).isNull();
    }

    @Test
    void shouldReturnAuthenticatedUserInfoWhenPrincipalExists() {
        OAuth2User principal = org.mockito.Mockito.mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn("customer@example.com");

        var response = controller.user(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isAuthenticated()).isTrue();
        assertThat(response.getBody().authenticatedUser().username()).isEqualTo("customer@example.com");
    }
}
