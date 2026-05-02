package com.yas.cart.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private static final class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
    }

    private final TestFallbackHandler handler = new TestFallbackHandler();

    @Test
    void handleBodilessFallback_ShouldRethrowOriginalThrowable() {
        RuntimeException exception = new RuntimeException("bodiless fallback");

        RuntimeException thrown = assertThrows(RuntimeException.class,
            () -> handler.handleBodilessFallback(exception));

        assertSame(exception, thrown);
    }

    @Test
    void handleTypedFallback_ShouldRethrowOriginalThrowable() {
        RuntimeException exception = new RuntimeException("typed fallback");

        RuntimeException thrown = assertThrows(RuntimeException.class,
            () -> handler.handleTypedFallback(exception));

        assertSame(exception, thrown);
    }
}
