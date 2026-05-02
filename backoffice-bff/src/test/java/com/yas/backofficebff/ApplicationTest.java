package com.yas.backofficebff;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

class ApplicationTest {

    @Test
    void shouldHaveSpringBootApplicationAnnotation() {
        assertTrue(Application.class.isAnnotationPresent(SpringBootApplication.class));
    }

    @Test
    void shouldHaveEnableWebFluxSecurityAnnotation() {
        assertTrue(Application.class.isAnnotationPresent(EnableWebFluxSecurity.class));
    }

    @Test
    void shouldLoadApplicationClass() {
        assertNotNull(Application.class);
    }
}
