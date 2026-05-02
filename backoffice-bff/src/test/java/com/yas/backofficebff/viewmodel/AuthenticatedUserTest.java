package com.yas.backofficebff.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AuthenticatedUserTest {

    @Test
    void shouldExposeUsernameValue() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser("backoffice-admin");

        assertEquals("backoffice-admin", authenticatedUser.username());
    }
}
