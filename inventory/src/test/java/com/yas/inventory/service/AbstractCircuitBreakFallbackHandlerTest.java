package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private final TestFallbackHandler handler = new TestFallbackHandler();

    @Test
    void testHandleBodilessFallback_rethrowsOriginalThrowable() {
        RuntimeException throwable = new RuntimeException("boom");

        RuntimeException thrown = assertThrows(RuntimeException.class,
            () -> handler.callHandleBodilessFallback(throwable));

        assertSame(throwable, thrown);
    }

    @Test
    void testHandleTypedFallback_rethrowsOriginalThrowable() {
        IllegalStateException throwable = new IllegalStateException("typed-boom");

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
            () -> handler.callHandleTypedFallback(throwable));

        assertSame(throwable, thrown);
    }

    private static class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        void callHandleBodilessFallback(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }

        <T> T callHandleTypedFallback(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }
    }
}
