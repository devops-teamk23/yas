package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private TestCircuitBreakFallbackHandler fallbackHandler;

    @BeforeEach
    void setUp() {
        fallbackHandler = new TestCircuitBreakFallbackHandler();
    }

    @Test
    void testHandleBodilessFallback_ThrowsException() {
        Throwable t = new RuntimeException("Test exception");

        Throwable thrown = assertThrows(RuntimeException.class, () -> {
            fallbackHandler.testHandleBodilessFallback(t);
        });

        assertEquals("Test exception", thrown.getMessage());
    }

    @Test
    void testHandleTypedFallback_ThrowsException() {
        Throwable t = new RuntimeException("Test typed exception");

        Throwable thrown = assertThrows(RuntimeException.class, () -> {
            fallbackHandler.testHandleTypedFallback(t);
        });

        assertEquals("Test typed exception", thrown.getMessage());
    }

    // Concrete implementation for testing the abstract class
    private static class TestCircuitBreakFallbackHandler extends AbstractCircuitBreakFallbackHandler {

        public void testHandleBodilessFallback(Throwable throwable) throws Throwable {
            super.handleBodilessFallback(throwable);
        }

        public <T> T testHandleTypedFallback(Throwable throwable) throws Throwable {
            return super.handleTypedFallback(throwable);
        }
    }
}
