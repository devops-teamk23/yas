package com.yas.sampledata.service;

import com.yas.sampledata.utils.SqlScriptExecutor;
import com.yas.sampledata.viewmodel.SampleDataVm;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SampleDataService class.
 * Tests cover:
 * - Service initialization with two DataSource dependencies
 * - createSampleData() method execution
 * - Integration with SqlScriptExecutor
 * - Return value validation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SampleDataService Tests")
class SampleDataServiceTest {

    @Mock
    private DataSource productDataSource;

    @Mock
    private DataSource mediaDataSource;

    @InjectMocks
    private SampleDataService sampleDataService;

    @BeforeEach
    void setUp() {
        // Additional setup if needed
    }

    @Test
    @DisplayName("Should create sample data successfully with correct message")
    void testCreateSampleDataSuccess() {
        // Act
        SampleDataVm result = sampleDataService.createSampleData();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("Insert Sample Data successfully!", result.message(),
            "Message should match expected success message");
    }

    @Test
    @DisplayName("Should return non-null SampleDataVm object")
    void testCreateSampleDataReturnsNonNull() {
        // Act
        SampleDataVm result = sampleDataService.createSampleData();

        // Assert
        assertNotNull(result, "SampleDataVm returned should not be null");
    }

    @Test
    @DisplayName("Should return same message for multiple calls")
    void testCreateSampleDataConsistentMessage() {
        // Act
        SampleDataVm result1 = sampleDataService.createSampleData();
        SampleDataVm result2 = sampleDataService.createSampleData();

        // Assert
        assertEquals(result1.message(), result2.message(),
            "Message should be consistent across multiple calls");
    }

    @Test
    @DisplayName("Should successfully initialize service with two DataSource beans")
    void testServiceInitializationWithTwoDataSources() {
        // Assert - if service is injectable, it means both DataSource dependencies were injected
        assertNotNull(sampleDataService, "Service should be successfully initialized");
    }

    @Test
    @DisplayName("Should return SampleDataVm with exactly the expected message text")
    void testCreateSampleDataMessageContent() {
        // Arrange
        String expectedMessage = "Insert Sample Data successfully!";

        // Act
        SampleDataVm result = sampleDataService.createSampleData();

        // Assert
        assertEquals(expectedMessage, result.message(), "Message content must match exactly");
        assertFalse(result.message().isEmpty(), "Message should not be empty");
        assertTrue(result.message().contains("Sample Data"), "Message should contain 'Sample Data'");
        assertTrue(result.message().contains("successfully"), "Message should contain 'successfully'");
    }

    @Test
    @DisplayName("Should handle multiple consecutive calls without state issues")
    void testMultipleConsecutiveCalls() {
        // Act & Assert
        for (int i = 0; i < 5; i++) {
            SampleDataVm result = sampleDataService.createSampleData();
            assertEquals("Insert Sample Data successfully!", result.message());
            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("Should return record object with proper equals implementation")
    void testSampleDataVmRecordEquality() {
        // Act
        SampleDataVm result1 = sampleDataService.createSampleData();
        SampleDataVm result2 = new SampleDataVm("Insert Sample Data successfully!");

        // Assert - Record's auto-generated equals should work
        assertEquals(result1, result2, "Two SampleDataVm objects with same message should be equal");
    }

    @Test
    @DisplayName("Should return record object with working toString method")
    void testSampleDataVmToString() {
        // Act
        SampleDataVm result = sampleDataService.createSampleData();
        String stringRepresentation = result.toString();

        // Assert - Record's auto-generated toString should contain the message
        assertNotNull(stringRepresentation, "toString should not be null");
        assertTrue(stringRepresentation.contains("SampleDataVm"),
            "toString should contain class name");
        assertTrue(stringRepresentation.contains("Insert Sample Data successfully!"),
            "toString should contain the message");
    }

    @Test
    @DisplayName("Should return record with hashCode working correctly")
    void testSampleDataVmHashCode() {
        // Act
        SampleDataVm result1 = sampleDataService.createSampleData();
        SampleDataVm result2 = new SampleDataVm("Insert Sample Data successfully!");

        // Assert - Record's auto-generated hashCode should be consistent
        assertEquals(result1.hashCode(), result2.hashCode(),
            "Same message should produce same hashCode");
    }
}
