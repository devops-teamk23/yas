package com.yas.sampledata.viewmodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ViewModel classes (SampleDataVm and ErrorVm).
 * Tests cover:
 * - Record instantiation
 * - Accessor methods
 * - equals() and hashCode()
 * - toString()
 * - Constructors (primary and compact)
 * - Parameter validation
 */
@DisplayName("ViewModels Tests")
class ViewModelsTest {

    @Nested
    @DisplayName("SampleDataVm Record Tests")
    class SampleDataVmTests {

        @Test
        @DisplayName("Should create SampleDataVm with message parameter")
        void testCreateSampleDataVm() {
            // Act
            SampleDataVm vm = new SampleDataVm("Test message");

            // Assert
            assertNotNull(vm, "SampleDataVm should be created successfully");
        }

        @Test
        @DisplayName("Should access message via accessor method")
        void testMessageAccessor() {
            // Arrange
            String testMessage = "Test message content";

            // Act
            SampleDataVm vm = new SampleDataVm(testMessage);
            String result = vm.message();

            // Assert
            assertEquals(testMessage, result, "message() should return the set value");
        }

        @Test
        @DisplayName("Should create SampleDataVm with empty string")
        void testCreateWithEmptyString() {
            // Act
            SampleDataVm vm = new SampleDataVm("");

            // Assert
            assertEquals("", vm.message(), "Should handle empty string");
        }

        @Test
        @DisplayName("Should handle special characters in message")
        void testSpecialCharactersInMessage() {
            // Arrange
            String specialMessage = "Test!@#$%^&*()_+-=[]{}|;:',.<>?/";

            // Act
            SampleDataVm vm = new SampleDataVm(specialMessage);

            // Assert
            assertEquals(specialMessage, vm.message(), "Should preserve special characters");
        }

        @Test
        @DisplayName("Should handle very long message string")
        void testVeryLongMessage() {
            // Arrange
            String longMessage = "a".repeat(10000);

            // Act
            SampleDataVm vm = new SampleDataVm(longMessage);

            // Assert
            assertEquals(longMessage, vm.message(), "Should handle very long strings");
            assertEquals(10000, vm.message().length(), "Message length should be correct");
        }

        @Test
        @DisplayName("Should have working equals() for same messages")
        void testEqualsWithSameMessage() {
            // Act
            SampleDataVm vm1 = new SampleDataVm("Same message");
            SampleDataVm vm2 = new SampleDataVm("Same message");

            // Assert
            assertEquals(vm1, vm2, "Records with same message should be equal");
        }

        @Test
        @DisplayName("Should have working equals() for different messages")
        void testEqualsWithDifferentMessages() {
            // Act
            SampleDataVm vm1 = new SampleDataVm("Message 1");
            SampleDataVm vm2 = new SampleDataVm("Message 2");

            // Assert
            assertNotEquals(vm1, vm2, "Records with different messages should not be equal");
        }

        @Test
        @DisplayName("Should have consistent hashCode for same messages")
        void testHashCodeConsistency() {
            // Act
            SampleDataVm vm1 = new SampleDataVm("Test");
            SampleDataVm vm2 = new SampleDataVm("Test");

            // Assert
            assertEquals(vm1.hashCode(), vm2.hashCode(), 
                "Same messages should produce same hashCode");
        }

        @Test
        @DisplayName("Should have different hashCode for different messages")
        void testHashCodeDifference() {
            // Act
            SampleDataVm vm1 = new SampleDataVm("Message 1");
            SampleDataVm vm2 = new SampleDataVm("Message 2");

            // Assert
            assertNotEquals(vm1.hashCode(), vm2.hashCode(), 
                "Different messages should (likely) produce different hashCode");
        }

        @Test
        @DisplayName("Should have working toString() method")
        void testToString() {
            // Arrange
            String message = "Test message";

            // Act
            SampleDataVm vm = new SampleDataVm(message);
            String stringRepresentation = vm.toString();

            // Assert
            assertNotNull(stringRepresentation, "toString() should not be null");
            assertTrue(stringRepresentation.contains("SampleDataVm"), 
                "toString() should contain class name");
            assertTrue(stringRepresentation.contains(message), 
                "toString() should contain message value");
        }

        @Test
        @DisplayName("Should handle null message (if allowed by design)")
        void testNullMessage() {
            // Act & Assert - Depends on whether null is allowed
            try {
                SampleDataVm vm = new SampleDataVm(null);
                assertNull(vm.message(), "Should handle null message");
            } catch (NullPointerException e) {
                // Also acceptable if nulls are not allowed
                assertTrue(true, "NullPointerException acceptable if nulls not allowed");
            }
        }

        @Test
        @DisplayName("Should be a record with proper structure")
        void testIsRecord() {
            // Act
            SampleDataVm vm = new SampleDataVm("Test");

            // Assert
            assertNotNull(vm, "Should create record instance");
            assertTrue(vm.getClass().isRecord(), "Should be a record");
        }

        @Test
        @DisplayName("Should have exactly one component (message)")
        void testComponentCount() {
            // Act
            SampleDataVm vm = new SampleDataVm("Test");

            // Assert
            var components = vm.getClass().getRecordComponents();
            assertEquals(1, components.length, "Should have exactly one record component");
            assertEquals("message", components[0].getName(), 
                "Component should be named 'message'");
        }
    }

    @Nested
    @DisplayName("ErrorVm Record Tests")
    class ErrorVmTests {

        @Test
        @DisplayName("Should create ErrorVm with all four parameters")
        void testCreateErrorVmWithAllParameters() {
            // Arrange
            List<String> fieldErrors = List.of("field1", "field2");

            // Act
            ErrorVm vm = new ErrorVm("400", "Bad Request", "Invalid input", fieldErrors);

            // Assert
            assertNotNull(vm, "ErrorVm should be created successfully");
        }

        @Test
        @DisplayName("Should create ErrorVm with three parameters (compact constructor)")
        void testCreateErrorVmWithCompactConstructor() {
            // Act
            ErrorVm vm = new ErrorVm("404", "Not Found", "Resource not found");

            // Assert
            assertNotNull(vm, "ErrorVm should be created with three parameters");
        }

        @Test
        @DisplayName("Should initialize fieldErrors as empty list in compact constructor")
        void testCompactConstructorInitializesEmptyFieldErrors() {
            // Act
            ErrorVm vm = new ErrorVm("400", "Bad Request", "Invalid");

            // Assert
            assertNotNull(vm.fieldErrors(), "fieldErrors should not be null");
            assertTrue(vm.fieldErrors().isEmpty(), "fieldErrors should be empty for compact constructor");
        }

        @Test
        @DisplayName("Should access statusCode via accessor")
        void testStatusCodeAccessor() {
            // Act
            ErrorVm vm = new ErrorVm("500", "Error", "Server error");
            String statusCode = vm.statusCode();

            // Assert
            assertEquals("500", statusCode, "statusCode() should return set value");
        }

        @Test
        @DisplayName("Should access title via accessor")
        void testTitleAccessor() {
            // Act
            ErrorVm vm = new ErrorVm("403", "Forbidden", "Access denied");
            String title = vm.title();

            // Assert
            assertEquals("Forbidden", title, "title() should return set value");
        }

        @Test
        @DisplayName("Should access detail via accessor")
        void testDetailAccessor() {
            // Act
            ErrorVm vm = new ErrorVm("401", "Unauthorized", "Invalid credentials");
            String detail = vm.detail();

            // Assert
            assertEquals("Invalid credentials", detail, "detail() should return set value");
        }

        @Test
        @DisplayName("Should access fieldErrors via accessor")
        void testFieldErrorsAccessor() {
            // Arrange
            List<String> fieldErrors = List.of("email", "password");

            // Act
            ErrorVm vm = new ErrorVm("400", "Bad Request", "Validation failed", fieldErrors);
            List<String> result = vm.fieldErrors();

            // Assert
            assertEquals(fieldErrors, result, "fieldErrors() should return set value");
        }

        @Test
        @DisplayName("Should handle empty fieldErrors list")
        void testEmptyFieldErrorsList() {
            // Act
            ErrorVm vm = new ErrorVm("200", "OK", "Success", new ArrayList<>());

            // Assert
            assertTrue(vm.fieldErrors().isEmpty(), "Should handle empty list");
        }

        @Test
        @DisplayName("Should handle null statusCode")
        void testNullStatusCode() {
            // Act & Assert
            try {
                ErrorVm vm = new ErrorVm(null, "Error", "Message");
                assertNull(vm.statusCode(), "Should handle null statusCode");
            } catch (NullPointerException e) {
                assertTrue(true, "NullPointerException acceptable if nulls not allowed");
            }
        }

        @Test
        @DisplayName("Should handle HTTP status codes correctly")
        void testHttpStatusCodes() {
            // Arrange
            String[] statusCodes = {"200", "201", "400", "401", "403", "404", "500"};

            // Act & Assert
            for (String code : statusCodes) {
                ErrorVm vm = new ErrorVm(code, "Status", "Message");
                assertEquals(code, vm.statusCode());
            }
        }

        @Test
        @DisplayName("Should have working equals() for identical ErrorVm")
        void testEqualsWithIdenticalErrorVm() {
            // Arrange
            List<String> errors = List.of("field1", "field2");

            // Act
            ErrorVm vm1 = new ErrorVm("400", "Error", "Detail", errors);
            ErrorVm vm2 = new ErrorVm("400", "Error", "Detail", errors);

            // Assert
            assertEquals(vm1, vm2, "Identical ErrorVm records should be equal");
        }

        @Test
        @DisplayName("Should have working equals() for different ErrorVm")
        void testEqualsWithDifferentErrorVm() {
            // Act
            ErrorVm vm1 = new ErrorVm("400", "Bad Request", "Error 1");
            ErrorVm vm2 = new ErrorVm("500", "Server Error", "Error 2");

            // Assert
            assertNotEquals(vm1, vm2, "Different ErrorVm records should not be equal");
        }

        @Test
        @DisplayName("Should have consistent hashCode")
        void testHashCodeConsistency() {
            // Act
            ErrorVm vm1 = new ErrorVm("404", "Not Found", "Resource missing");
            ErrorVm vm2 = new ErrorVm("404", "Not Found", "Resource missing");

            // Assert
            assertEquals(vm1.hashCode(), vm2.hashCode(), 
                "Same ErrorVm should produce same hashCode");
        }

        @Test
        @DisplayName("Should have working toString()")
        void testToString() {
            // Act
            ErrorVm vm = new ErrorVm("400", "Bad Request", "Invalid data");
            String stringRepresentation = vm.toString();

            // Assert
            assertNotNull(stringRepresentation, "toString() should not be null");
            assertTrue(stringRepresentation.contains("ErrorVm"), 
                "toString() should contain class name");
            assertTrue(stringRepresentation.contains("Bad Request"), 
                "toString() should contain title");
        }

        @Test
        @DisplayName("Should handle multiple field errors")
        void testMultipleFieldErrors() {
            // Arrange
            List<String> fieldErrors = List.of("email", "password", "username", "phone");

            // Act
            ErrorVm vm = new ErrorVm("400", "Validation Error", "Multiple fields invalid", fieldErrors);

            // Assert
            assertEquals(4, vm.fieldErrors().size(), "Should handle multiple field errors");
            assertEquals(fieldErrors, vm.fieldErrors(), "Should preserve all field errors");
        }

        @Test
        @DisplayName("Should be a record with proper structure")
        void testIsRecord() {
            // Act
            ErrorVm vm = new ErrorVm("400", "Error", "Message");

            // Assert
            assertNotNull(vm, "Should create record instance");
            assertTrue(vm.getClass().isRecord(), "Should be a record");
        }

        @Test
        @DisplayName("Should have exactly four components")
        void testComponentCount() {
            // Act
            ErrorVm vm = new ErrorVm("400", "Error", "Message");

            // Assert
            var components = vm.getClass().getRecordComponents();
            assertEquals(4, components.length, "Should have exactly four record components");
        }

        @Test
        @DisplayName("Should handle special characters in error details")
        void testSpecialCharactersInErrorDetails() {
            // Arrange
            String detail = "Error: @#$%^&*() !@#$";

            // Act
            ErrorVm vm = new ErrorVm("400", "Bad Request", detail);

            // Assert
            assertEquals(detail, vm.detail(), "Should preserve special characters");
        }
    }
}
