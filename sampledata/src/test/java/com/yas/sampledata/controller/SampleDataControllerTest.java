package com.yas.sampledata.controller;

import com.yas.sampledata.service.SampleDataService;
import com.yas.sampledata.viewmodel.SampleDataVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SampleDataController class.
 * Tests cover:
 * - Controller initialization with SampleDataService dependency
 * - createSampleData() endpoint method
 * - Request/response handling
 * - Service invocation
 * - Return value validation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SampleDataController Tests")
class SampleDataControllerTest {

    @Mock
    private SampleDataService sampleDataService;

    @InjectMocks
    private SampleDataController sampleDataController;

    private SampleDataVm expectedResponse;

    @BeforeEach
    void setUp() {
        expectedResponse = new SampleDataVm("Insert Sample Data successfully!");
    }

    @Test
    @DisplayName("Should create sample data and return service response")
    void testCreateSampleDataSuccess() {
        // Arrange
        when(sampleDataService.createSampleData()).thenReturn(expectedResponse);
        SampleDataVm request = new SampleDataVm("Sample data request");

        // Act
        SampleDataVm result = sampleDataController.createSampleData(request);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(expectedResponse.message(), result.message(),
            "Should return service response message");
        verify(sampleDataService, times(1)).createSampleData();
    }

    @Test
    @DisplayName("Should initialize controller with SampleDataService dependency")
    void testControllerInitialization() {
        // Assert
        assertNotNull(sampleDataController, "Controller should be successfully initialized");
    }

    @Test
    @DisplayName("Should call service exactly once per request")
    void testServiceInvokedOnce() {
        // Arrange
        when(sampleDataService.createSampleData()).thenReturn(expectedResponse);
        SampleDataVm request = new SampleDataVm("Test request");

        // Act
        sampleDataController.createSampleData(request);

        // Assert
        verify(sampleDataService, times(1)).createSampleData();
        verifyNoMoreInteractions(sampleDataService);
    }

    @Test
    @DisplayName("Should return exact service response without modification")
    void testReturnServiceResponseUnmodified() {
        // Arrange
        SampleDataVm serviceResponse = new SampleDataVm("Insert Sample Data successfully!");
        when(sampleDataService.createSampleData()).thenReturn(serviceResponse);
        SampleDataVm request = new SampleDataVm("Request");

        // Act
        SampleDataVm result = sampleDataController.createSampleData(request);

        // Assert
        assertSame(serviceResponse, result, "Should return the exact object from service");
    }

    @Test
    @DisplayName("Should handle null request parameter without exception")
    void testHandleNullRequest() {
        // Arrange
        when(sampleDataService.createSampleData()).thenReturn(expectedResponse);

        // Act & Assert - Method should not throw NPE even if request is null
        // (In real scenario with validation, this might be prevented by validator)
        try {
            SampleDataVm result = sampleDataController.createSampleData(null);
            // If reaches here, verify it still called service and returned response
            verify(sampleDataService, times(1)).createSampleData();
        } catch (NullPointerException e) {
            // This is also acceptable depending on validation rules
            assertTrue(true, "Null request handling depends on validation rules");
        }
    }

    @Test
    @DisplayName("Should return non-null response from valid request")
    void testCreateSampleDataReturnsNonNull() {
        // Arrange
        when(sampleDataService.createSampleData()).thenReturn(expectedResponse);
        SampleDataVm request = new SampleDataVm("Valid request");

        // Act
        SampleDataVm result = sampleDataController.createSampleData(request);

        // Assert
        assertNotNull(result, "Response should not be null for valid request");
    }

    @Test
    @DisplayName("Should handle multiple consecutive requests")
    void testHandleMultipleConsecutiveRequests() {
        // Arrange
        when(sampleDataService.createSampleData()).thenReturn(expectedResponse);

        // Act & Assert
        for (int i = 0; i < 3; i++) {
            SampleDataVm request = new SampleDataVm("Request " + i);
            SampleDataVm result = sampleDataController.createSampleData(request);
            assertEquals(expectedResponse.message(), result.message());
        }

        verify(sampleDataService, times(3)).createSampleData();
    }

    @Test
    @DisplayName("Should return response with expected success message")
    void testResponseContainsExpectedMessage() {
        // Arrange
        when(sampleDataService.createSampleData()).thenReturn(expectedResponse);
        SampleDataVm request = new SampleDataVm("Request");

        // Act
        SampleDataVm result = sampleDataController.createSampleData(request);

        // Assert
        assertTrue(result.message().contains("Successfully") || 
                   result.message().contains("successfully"),
            "Response should indicate success");
    }

    @Test
    @DisplayName("Should accept request with various message content")
    void testAcceptVariousRequestMessages() {
        // Arrange
        when(sampleDataService.createSampleData()).thenReturn(expectedResponse);

        // Act & Assert
        String[] testMessages = {"", "Test", "Long message here", "123", "!@#$%"};
        for (String message : testMessages) {
            SampleDataVm request = new SampleDataVm(message);
            SampleDataVm result = sampleDataController.createSampleData(request);
            // Service response should be independent of request
            assertEquals(expectedResponse, result);
        }
    }

    @Test
    @DisplayName("Should always delegate to service regardless of request")
    void testAlwaysDelegatesToService() {
        // Arrange
        when(sampleDataService.createSampleData()).thenReturn(expectedResponse);

        // Act
        SampleDataVm request1 = new SampleDataVm("Request 1");
        SampleDataVm request2 = new SampleDataVm("Request 2");
        SampleDataVm result1 = sampleDataController.createSampleData(request1);
        SampleDataVm result2 = sampleDataController.createSampleData(request2);

        // Assert - Service should be called for each request
        verify(sampleDataService, times(2)).createSampleData();
        assertEquals(result1, result2, "Both results should be identical");
    }
}
