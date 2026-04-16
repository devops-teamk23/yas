package com.yas.recommendation.service;

import com.yas.recommendation.configuration.RecommendationConfig;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService class.
 * Tests focus on product detail retrieval from external API and error handling.
 */
@DisplayName("ProductService Unit Tests")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private ProductService productService;

    @Mock
    private RestClient restClient;

    @Mock
    private RecommendationConfig config;

    @Mock
    private RestClient.RequestHeadersUriSpec<?> requestSpec;

    @Mock
    private ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        productService = new ProductService(restClient, config);
    }

    @Test
    @DisplayName("Should retrieve product details successfully with valid product ID")
    void testGetProductDetail_Success() {
        // Arrange
        long productId = 123L;
        String apiUrl = "http://api.example.com";
        ProductDetailVm expectedProduct = mock(ProductDetailVm.class);
        when(expectedProduct.getId()).thenReturn(productId);

        when(config.getApiUrl()).thenReturn(apiUrl);
        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) requestSpec);
        when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
        when(requestSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(expectedProduct));

        // Act
        ProductDetailVm result = productService.getProductDetail(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        verify(config).getApiUrl();
        verify(restClient).get();
    }

    @Test
    @DisplayName("Should construct correct API URL with product ID")
    void testGetProductDetail_CorrectUrlConstruction() {
        // Arrange
        long productId = 456L;
        String apiUrl = "http://api.example.com";
        ProductDetailVm expectedProduct = mock(ProductDetailVm.class);

        when(config.getApiUrl()).thenReturn(apiUrl);
        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) requestSpec);
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
        assertTrue(capturedUri.toString().contains("/storefront/products/detail/" + productId),
            "URL should contain path with product ID");
    }

    @Test
    @DisplayName("Should handle null response from external API")
    void testGetProductDetail_NullResponse() {
        // Arrange
        long productId = 789L;
        when(config.getApiUrl()).thenReturn("http://api.example.com");
        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) requestSpec);
        when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
        when(requestSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(null));

        // Act
        ProductDetailVm result = productService.getProductDetail(productId);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should propagate RestClient exception")
    void testGetProductDetail_RestClientException() {
        // Arrange
        long productId = 999L;
        when(config.getApiUrl()).thenReturn("http://api.example.com");
        when(restClient.get()).thenThrow(new RestClientException("Connection failed"));

        // Act & Assert
        assertThrows(RestClientException.class,
            () -> productService.getProductDetail(productId),
            "Should propagate RestClientException");
    }

    @Test
    @DisplayName("Should call restClient.get() exactly once")
    void testGetProductDetail_CallCount() {
        // Arrange
        long productId = 111L;
        ProductDetailVm expectedProduct = mock(ProductDetailVm.class);

        when(config.getApiUrl()).thenReturn("http://api.example.com");
        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) requestSpec);
        when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
        when(requestSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(expectedProduct));

        // Act
        productService.getProductDetail(productId);

        // Assert
        verify(restClient, times(1)).get();
    }

    @Test
    @DisplayName("Should handle different product IDs correctly")
    void testGetProductDetail_MultipleIds() {
        // Arrange
        long[] productIds = {1L, 100L, 99999L};

        for (long productId : productIds) {
            ProductDetailVm product = mock(ProductDetailVm.class);
            when(product.getId()).thenReturn(productId);

            when(config.getApiUrl()).thenReturn("http://api.example.com");
            when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) requestSpec);
            when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
            when(requestSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(product));

            // Act
            ProductDetailVm result = productService.getProductDetail(productId);

            // Assert
            assertNotNull(result);
            assertEquals(productId, result.getId());
        }
    }
}
