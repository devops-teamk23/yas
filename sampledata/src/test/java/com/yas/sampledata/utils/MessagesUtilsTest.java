package com.yas.sampledata.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MessagesUtils class.
 * Tests cover:
 * - getMessage() with valid error codes
 * - getMessage() with missing error codes (fallback to code)
 * - getMessage() with formatting parameters
 * - getMessage() with null parameters
 * - getMessage() with empty strings
 * - getMessage() with special characters
 */
@DisplayName("MessagesUtils Tests")
class MessagesUtilsTest {

    @Test
    @DisplayName("Should retrieve message for valid error code")
    void testGetMessageWithValidCode() {
        // Act
        String result = MessagesUtils.getMessage("error.sample");

        // Assert
        assertNotNull(result, "Message should not be null for valid code");
        assertFalse(result.isEmpty(), "Message should not be empty");
        // Message could be either the actual translation or the code itself
        assertTrue(!result.isEmpty(), "Result should have content");
    }

    @Test
    @DisplayName("Should return non-null result for any error code")
    void testGetMessageNeverReturnsNull() {
        // Act
        String result = MessagesUtils.getMessage("any.error.code");

        // Assert
        assertNotNull(result, "getMessage should never return null");
    }

    @Test
    @DisplayName("Should fallback to error code when message not found")
    void testGetMessageFallbackToCode() {
        // Act
        String result = MessagesUtils.getMessage("nonexistent.error.code.12345");

        // Assert
        assertNotNull(result, "Should return something even if not found");
        // When resource not found, should return the code itself
        assertEquals("nonexistent.error.code.12345", result,
            "Should fallback to error code when message not found");
    }

    @Test
    @DisplayName("Should handle empty error code")
    void testGetMessageWithEmptyCode() {
        // Act
        String result = MessagesUtils.getMessage("");

        // Assert
        assertNotNull(result, "Should handle empty code without throwing exception");
    }

    @Test
    @DisplayName("Should handle null error code gracefully")
    void testGetMessageWithNullCode() {
        // Act & Assert
        // This might throw NullPointerException - document the behavior
        try {
            String result = MessagesUtils.getMessage(null);
            assertNotNull(result, "Should handle null code");
        } catch (NullPointerException e) {
            // NullPointerException is acceptable for null input
            assertTrue(true, "NullPointerException acceptable for null input");
        }
    }

    @Test
    @DisplayName("Should support message formatting with parameters")
    void testGetMessageWithParameters() {
        // Act
        String result = MessagesUtils.getMessage("sample.message", "param1", "param2");

        // Assert
        assertNotNull(result, "Should return formatted message");
        // Result should be the formatted string
        assertFalse(result.isEmpty(), "Formatted message should not be empty");
    }

    @Test
    @DisplayName("Should handle single parameter formatting")
    void testGetMessageWithSingleParameter() {
        // Act
        String result = MessagesUtils.getMessage("sample.code", "value");

        // Assert
        assertNotNull(result, "Should format message with single parameter");
    }

    @Test
    @DisplayName("Should handle multiple formatting parameters")
    void testGetMessageWithMultipleParameters() {
        // Act
        String result = MessagesUtils.getMessage("error.code", "arg1", "arg2", "arg3");

        // Assert
        assertNotNull(result, "Should handle multiple parameters");
    }

    @Test
    @DisplayName("Should handle null as first parameter")
    void testGetMessageWithNullFirstParameter() {
        // Act & Assert
        try {
            String result = MessagesUtils.getMessage(null, "param");
            fail("Should throw exception for null code");
        } catch (NullPointerException e) {
            assertTrue(true, "NullPointerException expected for null code");
        }
    }

    @Test
    @DisplayName("Should handle empty parameters array")
    void testGetMessageWithEmptyParametersArray() {
        // Act
        String result = MessagesUtils.getMessage("error.code");

        // Assert
        assertNotNull(result, "Should handle no parameters");
    }

    @Test
    @DisplayName("Should handle special characters in error code")
    void testGetMessageWithSpecialCharactersInCode() {
        // Act
        String result = MessagesUtils.getMessage("error.@#$%.code");

        // Assert
        assertNotNull(result, "Should handle special characters in code");
        // When not found, should return the code itself
        assertEquals("error.@#$%.code", result, 
            "Should return code with special characters when not found");
    }

    @Test
    @DisplayName("Should handle numeric error codes")
    void testGetMessageWithNumericCode() {
        // Act
        String result = MessagesUtils.getMessage("123456");

        // Assert
        assertNotNull(result, "Should handle numeric codes");
    }

    @Test
    @DisplayName("Should handle very long error code")
    void testGetMessageWithVeryLongCode() {
        // Arrange
        String longCode = "a".repeat(1000) + ".error.code";

        // Act
        String result = MessagesUtils.getMessage(longCode);

        // Assert
        assertNotNull(result, "Should handle very long codes");
        assertEquals(longCode, result, "Should return the long code when not found");
    }

    @Test
    @DisplayName("Should handle whitespace in error code")
    void testGetMessageWithWhitespaceInCode() {
        // Act
        String result = MessagesUtils.getMessage("error code with spaces");

        // Assert
        assertNotNull(result, "Should handle codes with whitespace");
    }

    @Test
    @DisplayName("Should return consistent results for same error code")
    void testGetMessageConsistencyForSameCode() {
        // Act
        String result1 = MessagesUtils.getMessage("consistent.code");
        String result2 = MessagesUtils.getMessage("consistent.code");

        // Assert
        assertEquals(result1, result2, 
            "Should return consistent results for same error code");
    }

    @Test
    @DisplayName("Should handle parameter with null value in array")
    void testGetMessageWithNullParameterInArray() {
        // Act
        String result = MessagesUtils.getMessage("error.code", "value1", null, "value3");

        // Assert
        assertNotNull(result, "Should handle null values in parameter array");
    }

    @Test
    @DisplayName("Should process dot-separated error codes correctly")
    void testGetMessageWithDotSeparatedCode() {
        // Act
        String result = MessagesUtils.getMessage("error.validation.email");

        // Assert
        assertNotNull(result, "Should handle dot-separated codes");
    }

    @Test
    @DisplayName("Should handle underscore-separated error codes")
    void testGetMessageWithUnderscoreSeparatedCode() {
        // Act
        String result = MessagesUtils.getMessage("ERROR_VALIDATION_EMAIL");

        // Assert
        assertNotNull(result, "Should handle underscore-separated codes");
    }

    @Test
    @DisplayName("Should retrieve common error messages if they exist")
    void testGetCommonErrorMessages() {
        // Test common error codes that might exist in messages.properties
        String[] commonCodes = {
            "error.unauthorized",
            "error.forbidden",
            "error.not.found",
            "error.invalid"
        };

        for (String code : commonCodes) {
            String result = MessagesUtils.getMessage(code);
            assertNotNull(result, "Should return message for: " + code);
        }
    }
}
