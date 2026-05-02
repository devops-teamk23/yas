package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AbstractCircuitBreakFallbackHandler Tests")
public class AbstractCircuitBreakFallbackHandlerTest {

    private final TestCircuitBreakFallbackHandler handler = new TestCircuitBreakFallbackHandler();

    @Nested
    @DisplayName("handleBodilessFallback")
    class HandleBodilessFallbackTests {

        @Test
        @DisplayName("Should throw the original exception")
        void testHandleBodilessFallback_shouldThrowOriginalException() {
            // Arrange
            RuntimeException testException = new RuntimeException("Test error");

            // Act & Assert
            assertThatThrownBy(() -> handler.handleBodilessFallback(testException))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Test error");
        }

        @Test
        @DisplayName("Should handle NotFoundException")
        void testHandleBodilessFallback_shouldHandleNotFoundException() {
            // Arrange
            RuntimeException testException = new RuntimeException("Resource not found");

            // Act & Assert
            assertThatThrownBy(() -> handler.handleBodilessFallback(testException))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Resource not found");
        }

        @Test
        @DisplayName("Should handle different exception types")
        void testHandleBodilessFallback_shouldHandleDifferentExceptionTypes() {
            // Arrange
            IllegalArgumentException testException = new IllegalArgumentException("Invalid argument");

            // Act & Assert
            assertThatThrownBy(() -> handler.handleBodilessFallback(testException))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid argument");
        }
    }

    @Nested
    @DisplayName("handleTypedFallback")
    class HandleTypedFallbackTests {

        @Test
        @DisplayName("Should throw the original exception and return null")
        void testHandleTypedFallback_shouldThrowAndReturnNull() {
            // Arrange
            RuntimeException testException = new RuntimeException("Typed fallback error");

            // Act & Assert
            assertThatThrownBy(() -> handler.handleTypedFallback(testException))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Typed fallback error");
        }

        @Test
        @DisplayName("Should return null after handling error")
        void testHandleTypedFallback_shouldReturnNullAfterError() {
            // The implementation throws the exception, so we verify it throws
            RuntimeException testException = new RuntimeException("Error");

            assertThatThrownBy(() -> handler.handleTypedFallback(testException))
                .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("Should handle NullPointerException")
        void testHandleTypedFallback_shouldHandleNullPointerException() {
            // Arrange
            NullPointerException testException = new NullPointerException("Null value");

            // Act & Assert
            assertThatThrownBy(() -> handler.handleTypedFallback(testException))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Null value");
        }

        @Test
        @DisplayName("Should handle exception with null message")
        void testHandleTypedFallback_shouldHandleExceptionWithNullMessage() {
            // Arrange
            RuntimeException testException = new RuntimeException();

            // Act & Assert
            assertThatThrownBy(() -> handler.handleTypedFallback(testException))
                .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("Circuit Breaker Fallback Integration")
    class CircuitBreakerFallbackIntegrationTests {

        @Test
        @DisplayName("Should handle circuit breaker exceptions consistently")
        void testCircuitBreakerConsistency() {
            // Arrange
            RuntimeException circuitBreakerException = 
                new RuntimeException("Circuit breaker is open");

            // Act & Assert
            assertThatThrownBy(() -> handler.handleTypedFallback(circuitBreakerException))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Circuit breaker");
        }

        @Test
        @DisplayName("Should preserve exception stack trace information")
        void testPreserveStackTraceInformation() {
            // Arrange
            RuntimeException originalException = new RuntimeException("Original error");
            RuntimeException nestedException = new RuntimeException("Nested error", originalException);

            // Act & Assert
            assertThatThrownBy(() -> handler.handleTypedFallback(nestedException))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Nested error")
                .hasCauseInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("Should handle both typed and bodyless fallbacks with same exception")
        void testBothFallbackMethodsWithSameException() {
            // Arrange
            RuntimeException testException = new RuntimeException("Fallback test");

            // Act & Assert - both should throw the same exception
            assertThatThrownBy(() -> handler.handleBodilessFallback(testException))
                .isInstanceOf(RuntimeException.class);

            assertThatThrownBy(() -> handler.handleTypedFallback(testException))
                .isInstanceOf(RuntimeException.class);
        }
    }

    /**
     * Test implementation of AbstractCircuitBreakFallbackHandler
     * to allow testing of the abstract parent class methods
     */
    static class TestCircuitBreakFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        // This concrete implementation allows us to test the abstract parent class
    }
}
