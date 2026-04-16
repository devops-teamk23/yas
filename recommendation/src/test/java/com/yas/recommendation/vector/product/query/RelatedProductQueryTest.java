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
    private Object jdbcVectorService; // Package-private service, using Object to avoid import

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        relatedProductQuery = new RelatedProductQuery(null);
        // Inject mocked dependencies using reflection
        ReflectionTestUtils.setField(relatedProductQuery, "jdbcVectorService", jdbcVectorService);
        ReflectionTestUtils.setField(relatedProductQuery, "objectMapper", objectMapper);
    }

    @Test
    @DisplayName("Should return list of related products from similarity search")
    void testSimilaritySearch_Success() {
        // Arrange
        Long productId = 123L;
        Document doc1 = createMockDocument("product-1");
        Document doc2 = createMockDocument("product-2");
        List<Document> mockDocuments = Arrays.asList(doc1, doc2);

        RelatedProductVm product1 = new RelatedProductVm();
        product1.setId(1L);
        product1.setName("Related Product 1");

        RelatedProductVm product2 = new RelatedProductVm();
        product2.setId(2L);
        product2.setName("Related Product 2");

        when(jdbcVectorService.similarityProduct(productId, ProductDocument.class))
            .thenReturn(mockDocuments);
        when(objectMapper.convertValue(doc1.getMetadata(), RelatedProductVm.class))
            .thenReturn(product1);
        when(objectMapper.convertValue(doc2.getMetadata(), RelatedProductVm.class))
            .thenReturn(product2);

        // Act
        List<RelatedProductVm> results = relatedProductQuery.similaritySearch(productId);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("Related Product 1", results.get(0).getName());
        assertEquals("Related Product 2", results.get(1).getName());
        verify(jdbcVectorService).similarityProduct(productId, ProductDocument.class);
    }

    @Test
    @DisplayName("Should return empty list when no similar products found")
    void testSimilaritySearch_EmptyResult() {
        // Arrange
        Long productId = 456L;
        when(jdbcVectorService.similarityProduct(productId, ProductDocument.class))
            .thenReturn(Collections.emptyList());

        // Act
        List<RelatedProductVm> results = relatedProductQuery.similaritySearch(productId);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(jdbcVectorService).similarityProduct(productId, ProductDocument.class);
    }

    @Test
    @DisplayName("Should filter out documents with null metadata")
    void testSimilaritySearch_FilterNullMetadata() {
        // Arrange
        Long productId = 789L;
        Document validDoc = createMockDocument("product-1");
        Document nullMetadataDoc = new Document("content", null);
        Document anotherValidDoc = createMockDocument("product-2");
        
        List<Document> mockDocuments = Arrays.asList(validDoc, nullMetadataDoc, anotherValidDoc);

        RelatedProductVm product1 = new RelatedProductVm();
        product1.setId(1L);
        RelatedProductVm product2 = new RelatedProductVm();
        product2.setId(2L);

        when(jdbcVectorService.similarityProduct(productId, ProductDocument.class))
            .thenReturn(mockDocuments);
        when(objectMapper.convertValue(validDoc.getMetadata(), RelatedProductVm.class))
            .thenReturn(product1);
        when(objectMapper.convertValue(anotherValidDoc.getMetadata(), RelatedProductVm.class))
            .thenReturn(product2);

        // Act
        List<RelatedProductVm> results = relatedProductQuery.similaritySearch(productId);

        // Assert
        assertEquals(2, results.size(), "Documents with null metadata should be filtered");
        verify(objectMapper, times(2)).convertValue(any(), eq(RelatedProductVm.class));
    }

    @Test
    @DisplayName("Should handle documents with complex metadata")
    void testSimilaritySearch_ComplexMetadata() {
        // Arrange
        Long productId = 101L;
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("id", 1L);
        metadata.put("name", "Complex Product");
        metadata.put("nested", new HashMap<>());
        
        Document doc = new Document("content", metadata);
        List<Document> mockDocuments = List.of(doc);

        RelatedProductVm product = new RelatedProductVm();
        product.setId(1L);
        product.setName("Complex Product");

        when(jdbcVectorService.similarityProduct(productId, ProductDocument.class))
            .thenReturn(mockDocuments);
        when(objectMapper.convertValue(metadata, RelatedProductVm.class))
            .thenReturn(product);

        // Act
        List<RelatedProductVm> results = relatedProductQuery.similaritySearch(productId);

        // Assert
        assertEquals(1, results.size());
        assertEquals("Complex Product", results.get(0).getName());
    }

    @Test
    @DisplayName("Should return results in order from similarity search")
    void testSimilaritySearch_MaintainOrder() {
        // Arrange
        Long productId = 202L;
        List<Document> orderedDocuments = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            orderedDocuments.add(createMockDocument("product-" + i));
        }

        List<RelatedProductVm> orderedProducts = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            RelatedProductVm vm = new RelatedProductVm();
            vm.setId((long) i);
            vm.setName("Product " + i);
            orderedProducts.add(vm);
        }

        when(jdbcVectorService.similarityProduct(productId, ProductDocument.class))
            .thenReturn(orderedDocuments);

        for (int i = 0; i < 5; i++) {
            when(objectMapper.convertValue(orderedDocuments.get(i).getMetadata(), RelatedProductVm.class))
                .thenReturn(orderedProducts.get(i));
        }

        // Act
        List<RelatedProductVm> results = relatedProductQuery.similaritySearch(productId);

        // Assert
        assertEquals(5, results.size());
        for (int i = 0; i < 5; i++) {
            assertEquals((long) (i + 1), results.get(i).getId());
        }
    }

    @Test
    @DisplayName("Should handle large product IDs")
    void testSimilaritySearch_LargeProductId() {
        // Arrange
        Long largeProductId = Long.MAX_VALUE;
        Document doc = createMockDocument("product-1");
        RelatedProductVm product = new RelatedProductVm();

        when(jdbcVectorService.similarityProduct(largeProductId, ProductDocument.class))
            .thenReturn(List.of(doc));
        when(objectMapper.convertValue(doc.getMetadata(), RelatedProductVm.class))
            .thenReturn(product);

        // Act
        List<RelatedProductVm> results = relatedProductQuery.similaritySearch(largeProductId);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(jdbcVectorService).similarityProduct(largeProductId, ProductDocument.class);
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
    @DisplayName("Should handle exception from JDBC service")
    void testSimilaritySearch_Exception() {
        // Arrange
        Long productId = 303L;
        when(jdbcVectorService.similarityProduct(productId, ProductDocument.class))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> relatedProductQuery.similaritySearch(productId));
    }

    @Test
    @DisplayName("Should process multiple results with ObjectMapper conversion")
    void testSimilaritySearch_MultipleConversions() {
        // Arrange
        Long productId = 404L;
        List<Document> documents = new ArrayList<>();
        List<RelatedProductVm> products = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            documents.add(createMockDocument("product-" + i));
            RelatedProductVm vm = new RelatedProductVm();
            vm.setId((long) i);
            products.add(vm);
        }

        when(jdbcVectorService.similarityProduct(productId, ProductDocument.class))
            .thenReturn(documents);

        for (int i = 0; i < 10; i++) {
            when(objectMapper.convertValue(documents.get(i).getMetadata(), RelatedProductVm.class))
                .thenReturn(products.get(i));
        }

        // Act
        List<RelatedProductVm> results = relatedProductQuery.similaritySearch(productId);

        // Assert
        assertEquals(10, results.size());
        verify(objectMapper, times(10)).convertValue(any(), eq(RelatedProductVm.class));
    }

    @Test
    @DisplayName("Should handle mixed metadata - some null, some valid")
    void testSimilaritySearch_MixedMetadata() {
        // Arrange
        Long productId = 505L;
        Document validDoc1 = createMockDocument("product-1");
        Document nullMetadataDoc = new Document("content", null);
        Document validDoc2 = createMockDocument("product-2");
        Document anotherNullDoc = new Document("content", null);
        
        List<Document> documents = Arrays.asList(validDoc1, nullMetadataDoc, validDoc2, anotherNullDoc);

        RelatedProductVm product1 = new RelatedProductVm();
        product1.setId(1L);
        RelatedProductVm product2 = new RelatedProductVm();
        product2.setId(2L);

        when(jdbcVectorService.similarityProduct(productId, ProductDocument.class))
            .thenReturn(documents);
        when(objectMapper.convertValue(validDoc1.getMetadata(), RelatedProductVm.class))
            .thenReturn(product1);
        when(objectMapper.convertValue(validDoc2.getMetadata(), RelatedProductVm.class))
            .thenReturn(product2);

        // Act
        List<RelatedProductVm> results = relatedProductQuery.similaritySearch(productId);

        // Assert
        assertEquals(2, results.size(), "Only documents with valid metadata should be in results");
        assertEquals(1L, results.get(0).getId());
        assertEquals(2L, results.get(1).getId());
    }

    /**
     * Helper method to create a mock document with metadata
     */
    private Document createMockDocument(String contentPrefix) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("id", contentPrefix.hashCode());
        metadata.put("name", "Product: " + contentPrefix);
        return new Document(contentPrefix, metadata);
    }
}
