package com.yas.recommendation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.recommendation.configuration.RecommendationConfig;
import com.yas.recommendation.viewmodel.ImageVm;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

/**
 * Unit tests for ProductService class.
 * Tests cover API calls to fetch product details with various scenarios.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RecommendationConfig config;

    @Mock
    private RequestHeadersUriSpec<?> requestSpec;

    @Mock
    private ResponseSpec responseSpec;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(restClient, config);
    }

    @Nested
    @DisplayName("getProductDetail - Happy Path Tests")
    class GetProductDetailHappyPath {

        @Test
        @DisplayName("Should successfully retrieve product detail with valid product ID")
        void testGetProductDetailSuccess() {
            // Arrange
            long productId = 123L;
            String apiUrl = "http://product-service:8080/api";
            ProductDetailVm expectedProduct = createProductDetailVm(productId, "Test Product");

            when(config.getApiUrl()).thenReturn(apiUrl);
            when(restClient.get()).thenReturn(requestSpec);
            when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
            when(requestSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(expectedProduct));

            // Act
            ProductDetailVm result = productService.getProductDetail(productId);

            // Assert
            assertNotNull(result, "Product detail should not be null");
            assertEquals(productId, result.id(), "Product ID should match");
            assertEquals("Test Product", result.name(), "Product name should match");
            verify(restClient).get();
            verify(requestSpec).retrieve();
        }

        @Test
        @DisplayName("Should construct correct API URL with product ID")
        void testCorrectURLConstruction() {
            // Arrange
            long productId = 456L;
            String apiUrl = "http://product-service:9000/api";
            ProductDetailVm expectedProduct = createProductDetailVm(productId, "Another Product");

            when(config.getApiUrl()).thenReturn(apiUrl);
            when(restClient.get()).thenReturn(requestSpec);
            when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
            when(requestSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(expectedProduct));

            // Act
            productService.getProductDetail(productId);

            // Assert
            ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
            verify(requestSpec).uri(uriCaptor.capture());
            URI capturedUri = uriCaptor.getValue();
            assertNotNull(capturedUri, "URI should not be null");
            assertEquals("http://product-service:9000/api/storefront/products/detail/456", 
                    capturedUri.toString(), "URI should contain correct endpoint path");
        }

        @Test
        @DisplayName("Should handle product detail with all fields populated")
        void testGetProductDetailWithAllFields() {
            // Arrange
            long productId = 789L;
            ProductDetailVm fullProduct = new ProductDetailVm(
                    productId,
                    "Complete Product",
                    "Short description",
                    "Full description",
                    "Specification",
                    "SKU-123",
                    "GTIN-456",
                    "complete-product",
                    true,
                    true,
                    true,
                    true,
                    100.50,
                    90.00,
                    85.50,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    null,
                    new ArrayList<>()
            );

            when(config.getApiUrl()).thenReturn("http://api:8080");
            when(restClient.get()).thenReturn(requestSpec);
            when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
            when(requestSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(fullProduct));

            // Act
            ProductDetailVm result = productService.getProductDetail(productId);

            // Assert
            assertNotNull(result, "Product should not be null");
            assertEquals("Complete Product", result.name(), "Product name should match");
            assertEquals("SKU-123", result.sku(), "Product SKU should match");
            assertEquals(100.50, result.price(), "Product price should match");
            assertEquals(true, result.isPublished(), "Product published flag should match");
        }

        @Test
        @DisplayName("Should handle minimal product detail with required fields only")
        void testGetProductDetailMinimalFields() {
            // Arrange
            long productId = 111L;
            ProductDetailVm minimalProduct = new ProductDetailVm(
                    productId, "Minimal Product", null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null, null, null,
                    null, null, new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>()
            );

            when(config.getApiUrl()).thenReturn("http://api:8080");
            when(restClient.get()).thenReturn(requestSpec);
            when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
            when(requestSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(minimalProduct));

            // Act
            ProductDetailVm result = productService.getProductDetail(productId);

            // Assert
            assertNotNull(result, "Product should not be null");
            assertEquals(productId, result.id(), "Product ID should match");
        }
    }

    @Nested
    @DisplayName("getProductDetail - Edge Cases & Error Handling")
    class GetProductDetailEdgeCases {

        @Test
        @DisplayName("Should handle very large product ID")
        void testGetProductDetailWithLargeId() {
            // Arrange
            long largeProductId = Long.MAX_VALUE;
            ProductDetailVm product = createProductDetailVm(largeProductId, "Large ID Product");

            when(config.getApiUrl()).thenReturn("http://api:8080");
            when(restClient.get()).thenReturn(requestSpec);
            when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
            when(requestSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(product));

            // Act
            ProductDetailVm result = productService.getProductDetail(largeProductId);

            // Assert
            assertNotNull(result, "Product should not be null");
            assertEquals(largeProductId, result.id(), "Large product ID should be handled");
        }

        @Test
        @DisplayName("Should handle zero product ID")
        void testGetProductDetailWithZeroId() {
            // Arrange
            long zeroProductId = 0L;
            ProductDetailVm product = createProductDetailVm(zeroProductId, "Zero ID Product");

            when(config.getApiUrl()).thenReturn("http://api:8080");
            when(restClient.get()).thenReturn(requestSpec);
            when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
            when(requestSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(product));

            // Act
            ProductDetailVm result = productService.getProductDetail(zeroProductId);

            // Assert
            assertNotNull(result, "Product should not be null");
            assertEquals(zeroProductId, result.id(), "Zero ID should be handled");
        }

        @Test
        @DisplayName("Should handle one product ID")
        void testGetProductDetailWithOneId() {
            // Arrange
            long oneProductId = 1L;
            ProductDetailVm product = createProductDetailVm(oneProductId, "One ID Product");

            when(config.getApiUrl()).thenReturn("http://api:8080");
            when(restClient.get()).thenReturn(requestSpec);
            when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
            when(requestSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(product));

            // Act
            ProductDetailVm result = productService.getProductDetail(oneProductId);

            // Assert
            assertNotNull(result, "Product should not be null");
            assertEquals(oneProductId, result.id(), "One ID should be handled");
        }

        @Test
        @DisplayName("Should handle product with boolean flags as false")
        void testGetProductDetailWithFalseFlags() {
            // Arrange
            long productId = 222L;
            ProductDetailVm product = new ProductDetailVm(
                    productId, "Disabled Product", null, null, null, null, null, null,
                    false, false, false, false, null, null, null, null, null, null, null,
                    null, null, new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>()
            );

            when(config.getApiUrl()).thenReturn("http://api:8080");
            when(restClient.get()).thenReturn(requestSpec);
            when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
            when(requestSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(product));

            // Act
            ProductDetailVm result = productService.getProductDetail(productId);

            // Assert
            assertNotNull(result, "Product should not be null");
            assertEquals(false, result.isAllowedToOrder(), "Should handle false flags");
        }

        @Test
        @DisplayName("Should handle product with null return from API")
        void testGetProductDetailWithNullResponse() {
            // Arrange
            long productId = 333L;

            when(config.getApiUrl()).thenReturn("http://api:8080");
            when(restClient.get()).thenReturn(requestSpec);
            when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
            when(requestSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(null));

            // Act & Assert
            assertThrows(NullPointerException.class, 
                    () -> productService.getProductDetail(productId),
                    "Should handle null response gracefully");
        }

        @Test
        @DisplayName("Should handle REST client throwing exception")
        void testGetProductDetailWithRestClientException() {
            // Arrange
            long productId = 444L;

            when(config.getApiUrl()).thenReturn("http://api:8080");
            when(restClient.get()).thenThrow(new RuntimeException("Connection refused"));

            // Act & Assert
            assertThrows(RuntimeException.class, 
                    () -> productService.getProductDetail(productId),
                    "Should propagate REST client exceptions");
        }

        @Test
        @DisplayName("Should handle configuration with null API URL")
        void testGetProductDetailWithNullConfigUrl() {
            // Arrange
            long productId = 555L;

            when(config.getApiUrl()).thenReturn(null);
            when(restClient.get()).thenReturn(requestSpec);
            when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);

            // Act & Assert
            assertThrows(Exception.class, 
                    () -> productService.getProductDetail(productId),
                    "Should handle null API URL");
        }
    }

    @Nested
    @DisplayName("getProductDetail - Multiple Calls & State Tests")
    class GetProductDetailMultipleCalls {

        @Test
        @DisplayName("Should handle sequential calls with different product IDs")
        void testMultipleSequentialCalls() {
            // Arrange
            long productId1 = 10L;
            long productId2 = 20L;
            ProductDetailVm product1 = createProductDetailVm(productId1, "Product 1");
            ProductDetailVm product2 = createProductDetailVm(productId2, "Product 2");

            when(config.getApiUrl()).thenReturn("http://api:8080");
            when(restClient.get()).thenReturn(requestSpec);
            when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
            when(requestSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(product1))
                    .thenReturn(ResponseEntity.ok(product2));

            // Act
            ProductDetailVm result1 = productService.getProductDetail(productId1);
            ProductDetailVm result2 = productService.getProductDetail(productId2);

            // Assert
            assertEquals(productId1, result1.id(), "First call should return first product");
            assertEquals(productId2, result2.id(), "Second call should return second product");
            verify(restClient).get();
        }

        @Test
        @DisplayName("Should handle repeated calls with same product ID")
        void testRepeatedCallsWithSameId() {
            // Arrange
            long productId = 100L;
            ProductDetailVm product = createProductDetailVm(productId, "Repeated Product");

            when(config.getApiUrl()).thenReturn("http://api:8080");
            when(restClient.get()).thenReturn(requestSpec);
            when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
            when(requestSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(product));

            // Act
            ProductDetailVm result1 = productService.getProductDetail(productId);
            ProductDetailVm result2 = productService.getProductDetail(productId);

            // Assert
            assertEquals(result1.id(), result2.id(), "Same ID should return consistent results");
            assertEquals(productId, result2.id(), "Repeated calls should work correctly");
        }
    }

    @Nested
    @DisplayName("getProductDetail - Integration with Dependencies")
    class GetProductDetailDependencies {

        @Test
        @DisplayName("Should correctly call RestClient chain")
        void testRestClientChainCall() {
            // Arrange
            long productId = 600L;
            ProductDetailVm product = createProductDetailVm(productId, "Chain Test");

            when(config.getApiUrl()).thenReturn("http://api:8080");
            when(restClient.get()).thenReturn(requestSpec);
            when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
            when(requestSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(product));

            // Act
            productService.getProductDetail(productId);

            // Assert - Verify the call chain
            verify(restClient).get();
            verify(requestSpec).uri(any(URI.class));
            verify(requestSpec).retrieve();
            verify(responseSpec).toEntity(any(ParameterizedTypeReference.class));
        }

        @Test
        @DisplayName("Should only call configuration once per request")
        void testConfigurationCalledOnce() {
            // Arrange
            long productId = 700L;
            ProductDetailVm product = createProductDetailVm(productId, "Config Test");

            when(config.getApiUrl()).thenReturn("http://api:8080");
            when(restClient.get()).thenReturn(requestSpec);
            when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
            when(requestSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                    .thenReturn(ResponseEntity.ok(product));

            // Act
            productService.getProductDetail(productId);

            // Assert - Configuration should be accessed to get API URL
            verify(config).getApiUrl();
        }
    }

    // ============ Helper Methods ============

    /**
     * Creates a ProductDetailVm with minimal but valid data for testing.
     */
    private ProductDetailVm createProductDetailVm(long productId, String name) {
        return new ProductDetailVm(
                productId,
                name,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                new ArrayList<>()
        );
    }
}
