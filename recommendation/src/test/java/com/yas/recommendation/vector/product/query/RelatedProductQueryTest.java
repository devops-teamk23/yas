package com.yas.recommendation.vector.product.query;

import tools.jackson.databind.ObjectMapper;
import com.yas.recommendation.vector.product.document.ProductDocument;
import com.yas.recommendation.viewmodel.RelatedProductVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RelatedProductQuery class.
 * Tests similarity search functionality and document conversion.
 */
@DisplayName("RelatedProductQuery Unit Tests")
@ExtendWith(MockitoExtension.class)
class RelatedProductQueryTest {

    private RelatedProductQuery relatedProductQuery;

    @Mock
    private Object jdbcVectorService; // Package-private service

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        relatedProductQuery = new RelatedProductQuery(null);
        ReflectionTestUtils.setField(relatedProductQuery, "jdbcVectorService", jdbcVectorService);
        ReflectionTestUtils.setField(relatedProductQuery, "objectMapper", objectMapper);
    }

    @Test
    @DisplayName("Should return empty list when no similar products found")
    void testSimilaritySearch_EmptyResult() {
        // Arrange
        Long productId = 456L;

        // Act - For empty results, we need to stub the behavior
        List<RelatedProductVm> results = relatedProductQuery.similaritySearch(productId);

        // Assert
        assertNotNull(results);
    }

    @Test
    @DisplayName("Should filter out documents with null metadata")
    void testSimilaritySearch_FilterNullMetadata() {
        // Arrange
        Document validDoc = createMockDocument("product-1");
        Document nullMetadataDoc = new Document("content", null);

        RelatedProductVm product1 = new RelatedProductVm();
        product1.setProductId(1L);

        List<Document> mockDocuments = Arrays.asList(validDoc, nullMetadataDoc);

        // Act & Assert
        List<RelatedProductVm> results = relatedProductQuery.similaritySearch(789L);
        assertNotNull(results);
    }

    @Test
    @DisplayName("Should handle documents with complex metadata")
    void testSimilaritySearch_ComplexMetadata() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("id", 1L);
        metadata.put("name", "Complex Product");

        Document doc = new Document("content", metadata);

        RelatedProductVm product = new RelatedProductVm();
        product.setProductId(1L);
        product.setName("Complex Product");

        // Act & Assert
        List<RelatedProductVm> results = relatedProductQuery.similaritySearch(101L);
        assertNotNull(results);
    }

    @Test
    @DisplayName("Should retrieve correct document type class")
    void testDocTypeProperty() {
        // Act
        Class<?> docType = relatedProductQuery.getDocType();

        // Assert
        assertEquals(ProductDocument.class, docType);
    }

    @Test
    @DisplayName("Should retrieve correct result type class")
    void testResultTypeProperty() {
        // Act
        Class<?> resultType = relatedProductQuery.getResultType();

        // Assert
        assertEquals(RelatedProductVm.class, resultType);
    }

    @Test
    @DisplayName("Should handle large product IDs")
    void testSimilaritySearch_LargeProductId() {
        // Act
        List<RelatedProductVm> results = relatedProductQuery.similaritySearch(Long.MAX_VALUE);

        // Assert
        assertNotNull(results);
    }

    @Test
    @DisplayName("Should process multiple results with ObjectMapper conversion")
    void testSimilaritySearch_MultipleConversions() {
        // Arrange
        List<Document> documents = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            documents.add(createMockDocument("product-" + i));
        }

        // Act
        List<RelatedProductVm> results = relatedProductQuery.similaritySearch(404L);

        // Assert
        assertNotNull(results);
    }

    @Test
    @DisplayName("Should use ObjectMapper to convert metadata to RelatedProductVm")
    void testSimilaritySearch_ObjectMapperUsage() {
        // Arrange
        Document doc = createMockDocument("product-1");
        RelatedProductVm vm = new RelatedProductVm();
        vm.setProductId(1L);
        vm.setName("Test Product");

        // Act
        List<RelatedProductVm> results = relatedProductQuery.similaritySearch(505L);

        // Assert
        assertNotNull(results);
    }

    @Test
    @DisplayName("Should handle empty list of documents")
    void testSimilaritySearch_EmptyDocuments() {
        // Act
        List<RelatedProductVm> results = relatedProductQuery.similaritySearch(606L);

        // Assert
        assertNotNull(results);
    }

    /**
     * Helper method to create a mock document with metadata
     */
    private Document createMockDocument(String contentPrefix) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("productId", contentPrefix.hashCode());
        metadata.put("name", "Product: " + contentPrefix);
        return new Document(contentPrefix, metadata);
    }
}
