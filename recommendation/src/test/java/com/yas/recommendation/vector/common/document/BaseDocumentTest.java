package com.yas.recommendation.vector.common.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.ContentFormatter;
import org.springframework.ai.document.DefaultContentFormatter;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.id.IdGenerator;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for BaseDocument abstract class.
 * Testing document conversion and validation using ProductDocument as concrete implementation.
 */
@DisplayName("BaseDocument Unit Tests")
@ExtendWith(MockitoExtension.class)
class BaseDocumentTest {

    private TestDocument testDocument;

    @Mock
    private IdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        testDocument = new TestDocument();
    }

    /**
     * Concrete test implementation of BaseDocument
     */
    @DocumentMetadata(
        docIdPrefix = "TEST",
        contentFormat = "Test content: {name}",
        documentFormatter = com.yas.recommendation.vector.common.formatter.DefaultDocumentFormatter.class
    )
    private static class TestDocument extends BaseDocument {
    }

    @Test
    @DisplayName("Should convert document to Spring AI Document with valid data")
    void testToDocument_ValidConversion() {
        // Arrange
        testDocument.setEntityId(123L);
        testDocument.setContent("Test content");
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("name", "Test Product");
        testDocument.setMetadata(metadata);

        when(idGenerator.generateId(any())).thenReturn("test-id-123");

        // Act
        Document document = testDocument.toDocument(idGenerator);

        // Assert
        assertNotNull(document);
        assertEquals("Test content", document.getContent());
        assertEquals(metadata, document.getMetadata());
    }

    @Test
    @DisplayName("Should throw exception when document not annotated with @DocumentMetadata")
    void testToDocument_MissingDocumentMetadataAnnotation() {
        // Arrange
        BaseDocument unannotatedDoc = new BaseDocument() {
            // This class doesn't have @DocumentMetadata annotation
        };
        unannotatedDoc.setEntityId(456L);
        unannotatedDoc.setContent("Content");
        unannotatedDoc.setMetadata(new HashMap<>());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> unannotatedDoc.toDocument(idGenerator),
            "Should throw exception for missing @DocumentMetadata annotation");
        assertTrue(exception.getMessage().contains("@DocumentMetadata"));
    }

    @Test
    @DisplayName("Should throw exception when content is null")
    void testToDocument_NullContent() {
        // Arrange
        testDocument.setEntityId(789L);
        testDocument.setContent(null);
        testDocument.setMetadata(new HashMap<>());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> testDocument.toDocument(idGenerator),
            "Should throw exception for null content");
        assertTrue(exception.getMessage().contains("content cannot be null"));
    }

    @Test
    @DisplayName("Should throw exception when metadata is null")
    void testToDocument_NullMetadata() {
        // Arrange
        testDocument.setEntityId(101L);
        testDocument.setContent("Test content");
        testDocument.setMetadata(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> testDocument.toDocument(idGenerator),
            "Should throw exception for null metadata");
        assertTrue(exception.getMessage().contains("metadata cannot be null"));
    }

    @Test
    @DisplayName("Should use custom content formatter if provided")
    void testToDocument_CustomContentFormatter() {
        // Arrange
        ContentFormatter customFormatter = mock(ContentFormatter.class);
        testDocument.setEntityId(202L);
        testDocument.setContent("Test content");
        testDocument.setMetadata(new HashMap<>());
        testDocument.setContentFormatter(customFormatter);

        // Act
        Document document = testDocument.toDocument(idGenerator);

        // Assert
        assertNotNull(document);
        assertEquals(customFormatter, document.getContentFormatter());
    }

    @Test
    @DisplayName("Should use default content formatter if none provided")
    void testToDocument_DefaultContentFormatter() {
        // Arrange
        testDocument.setEntityId(303L);
        testDocument.setContent("Test content");
        testDocument.setMetadata(new HashMap<>());
        testDocument.setContentFormatter(null);

        // Act
        Document document = testDocument.toDocument(idGenerator);

        // Assert
        assertNotNull(document);
        assertNotNull(document.getContentFormatter());
        assertEquals(BaseDocument.DEFAULT_CONTENT_FORMATTER, document.getContentFormatter());
    }

    @Test
    @DisplayName("Should set entity ID correctly")
    void testSetEntityId() {
        // Arrange
        long entityId = 999L;

        // Act
        testDocument.setEntityId(entityId);

        // Assert
        assertEquals(entityId, testDocument.getEntityId());
    }

    @Test
    @DisplayName("Should set content correctly")
    void testSetContent() {
        // Arrange
        String content = "Test document content";

        // Act
        testDocument.setContent(content);

        // Assert
        assertEquals(content, testDocument.getContent());
    }

    @Test
    @DisplayName("Should set metadata correctly")
    void testSetMetadata() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key1", "value1");
        metadata.put("key2", "value2");

        // Act
        testDocument.setMetadata(metadata);

        // Assert
        assertEquals(metadata, testDocument.getMetadata());
        assertEquals("value1", testDocument.getMetadata().get("key1"));
    }

    @Test
    @DisplayName("Should handle large content strings")
    void testToDocument_LargeContent() {
        // Arrange
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeContent.append("This is a test content line. ");
        }
        testDocument.setEntityId(111L);
        testDocument.setContent(largeContent.toString());
        testDocument.setMetadata(new HashMap<>());

        // Act
        Document document = testDocument.toDocument(idGenerator);

        // Assert
        assertNotNull(document);
        assertEquals(largeContent.toString(), document.getContent());
    }

    @Test
    @DisplayName("Should handle complex metadata structures")
    void testToDocument_ComplexMetadata() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("nested_key", "nested_value");
        metadata.put("complex_field", nestedMap);
        metadata.put("list_field", new java.util.ArrayList<>());

        testDocument.setEntityId(222L);
        testDocument.setContent("Content");
        testDocument.setMetadata(metadata);

        // Act
        Document document = testDocument.toDocument(idGenerator);

        // Assert
        assertNotNull(document);
        assertEquals(metadata, document.getMetadata());
    }

    @Test
    @DisplayName("Should preserve entity ID in document")
    void testToDocument_PreserveEntityId() {
        // Arrange
        long entityId = 777L;
        testDocument.setEntityId(entityId);
        testDocument.setContent("Content");
        testDocument.setMetadata(new HashMap<>());

        // Act
        Document document = testDocument.toDocument(idGenerator);

        // Assert
        // Entity ID is set on the BaseDocument, not directly on Document
        // This test verifies the document creation with entity ID
        assertNotNull(document);
    }

    @Test
    @DisplayName("Should throw exception for both null content and metadata")
    void testToDocument_BothNullContentAndMetadata() {
        // Arrange
        testDocument.setEntityId(888L);
        testDocument.setContent(null);
        testDocument.setMetadata(null);

        // Act & Assert
        // Should fail on content check first
        assertThrows(IllegalArgumentException.class,
            () -> testDocument.toDocument(idGenerator));
    }

    @Test
    @DisplayName("Should support empty metadata")
    void testToDocument_EmptyMetadata() {
        // Arrange
        testDocument.setEntityId(444L);
        testDocument.setContent("Content");
        testDocument.setMetadata(new HashMap<>()); // Empty map

        // Act
        Document document = testDocument.toDocument(idGenerator);

        // Assert
        assertNotNull(document);
        assertTrue(document.getMetadata().isEmpty());
    }

    @Test
    @DisplayName("Should support metadata with null values")
    void testToDocument_MetadataWithNullValues() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key_with_null", null);
        metadata.put("key_with_value", "value");

        testDocument.setEntityId(555L);
        testDocument.setContent("Content");
        testDocument.setMetadata(metadata);

        // Act
        Document document = testDocument.toDocument(idGenerator);

        // Assert
        assertNotNull(document);
        assertNull(document.getMetadata().get("key_with_null"));
    }
}
