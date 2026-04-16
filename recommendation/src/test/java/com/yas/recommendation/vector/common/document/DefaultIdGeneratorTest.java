package com.yas.recommendation.vector.common.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DefaultIdGenerator class.
 * Tests UUID generation, consistency, and edge cases.
 */
@DisplayName("DefaultIdGenerator Unit Tests")
@ExtendWith(MockitoExtension.class)
class DefaultIdGeneratorTest {

    private DefaultIdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        idGenerator = new DefaultIdGenerator("product", 123L);
    }

    @Test
    @DisplayName("Should generate valid UUID format")
    void testGenerateId_ValidUuidFormat() {
        // Act
        String generatedId = idGenerator.generateId();

        // Assert
        assertNotNull(generatedId);
        assertDoesNotThrow(() -> UUID.fromString(generatedId));
    }

    @Test
    @DisplayName("Should generate consistent UUID for same prefix and identity")
    void testGenerateId_ConsistentGeneration() {
        // Arrange
        DefaultIdGenerator generator1 = new DefaultIdGenerator("product", 456L);
        DefaultIdGenerator generator2 = new DefaultIdGenerator("product", 456L);

        // Act
        String id1 = generator1.generateId();
        String id2 = generator2.generateId();

        // Assert
        assertEquals(id1, id2, "Same prefix and identity should generate same UUID");
    }

    @Test
    @DisplayName("Should generate different UUIDs for different identities")
    void testGenerateId_DifferentIdentities() {
        // Arrange
        DefaultIdGenerator generator1 = new DefaultIdGenerator("product", 789L);
        DefaultIdGenerator generator2 = new DefaultIdGenerator("product", 790L);

        // Act
        String id1 = generator1.generateId();
        String id2 = generator2.generateId();

        // Assert
        assertNotEquals(id1, id2, "Different identities should generate different UUIDs");
    }

    @Test
    @DisplayName("Should generate different UUIDs for different prefixes")
    void testGenerateId_DifferentPrefixes() {
        // Arrange
        DefaultIdGenerator generator1 = new DefaultIdGenerator("product", 999L);
        DefaultIdGenerator generator2 = new DefaultIdGenerator("category", 999L);

        // Act
        String id1 = generator1.generateId();
        String id2 = generator2.generateId();

        // Assert
        assertNotEquals(id1, id2, "Different prefixes should generate different UUIDs");
    }

    @Test
    @DisplayName("Should handle null prefix gracefully")
    void testGenerateId_NullPrefix() {
        // Arrange
        DefaultIdGenerator generator = new DefaultIdGenerator(null, 111L);

        // Act & Assert
        assertDoesNotThrow(() -> {
            String id = generator.generateId();
            assertNotNull(id);
            assertDoesNotThrow(() -> UUID.fromString(id));
        });
    }

    @Test
    @DisplayName("Should handle null identity gracefully")
    void testGenerateId_NullIdentity() {
        // Arrange
        DefaultIdGenerator generator = new DefaultIdGenerator("product", null);

        // Act & Assert
        assertDoesNotThrow(() -> {
            String id = generator.generateId();
            assertNotNull(id);
            assertDoesNotThrow(() -> UUID.fromString(id));
        });
    }

    @Test
    @DisplayName("Should handle edge case with zero identity")
    void testGenerateId_ZeroIdentity() {
        // Arrange
        DefaultIdGenerator generator = new DefaultIdGenerator("product", 0L);

        // Act
        String id = generator.generateId();

        // Assert
        assertNotNull(id);
        assertDoesNotThrow(() -> UUID.fromString(id));
    }

    @Test
    @DisplayName("Should handle edge case with negative identity")
    void testGenerateId_NegativeIdentity() {
        // Arrange
        DefaultIdGenerator generator = new DefaultIdGenerator("product", -123L);

        // Act
        String id = generator.generateId();

        // Assert
        assertNotNull(id);
        assertDoesNotThrow(() -> UUID.fromString(id));
    }

    @Test
    @DisplayName("Should handle edge case with max long value")
    void testGenerateId_MaxLongIdentity() {
        // Arrange
        DefaultIdGenerator generator = new DefaultIdGenerator("product", Long.MAX_VALUE);

        // Act
        String id = generator.generateId();

        // Assert
        assertNotNull(id);
        assertDoesNotThrow(() -> UUID.fromString(id));
    }

    @Test
    @DisplayName("Should handle edge case with min long value")
    void testGenerateId_MinLongIdentity() {
        // Arrange
        DefaultIdGenerator generator = new DefaultIdGenerator("product", Long.MIN_VALUE);

        // Act
        String id = generator.generateId();

        // Assert
        assertNotNull(id);
        assertDoesNotThrow(() -> UUID.fromString(id));
    }

    @Test
    @DisplayName("Should generate unique UUIDs for large number of different identities")
    void testGenerateId_UniquenessForManyIdentities() {
        // Arrange
        Set<String> generatedIds = new HashSet<>();
        int count = 1000;

        // Act
        for (int i = 0; i < count; i++) {
            DefaultIdGenerator generator = new DefaultIdGenerator("product", (long) i);
            generatedIds.add(generator.generateId());
        }

        // Assert
        assertEquals(count, generatedIds.size(), 
            "All generated IDs should be unique for different identities");
    }

    @Test
    @DisplayName("Should handle special characters in prefix")
    void testGenerateId_SpecialCharactersInPrefix() {
        // Arrange
        DefaultIdGenerator generator = new DefaultIdGenerator("product-category_123", 456L);

        // Act
        String id = generator.generateId();

        // Assert
        assertNotNull(id);
        assertDoesNotThrow(() -> UUID.fromString(id));
    }

    @Test
    @DisplayName("Should handle empty string prefix")
    void testGenerateId_EmptyStringPrefix() {
        // Arrange
        DefaultIdGenerator generator = new DefaultIdGenerator("", 789L);

        // Act
        String id = generator.generateId();

        // Assert
        assertNotNull(id);
        assertDoesNotThrow(() -> UUID.fromString(id));
    }

    @Test
    @DisplayName("Should generate UUID v5 (name-based)")
    void testGenerateId_IsNameBasedUuid() {
        // Arrange
        DefaultIdGenerator generator = new DefaultIdGenerator("product", 100L);
        String generatedId = generator.generateId();

        // Act
        UUID uuid = UUID.fromString(generatedId);

        // Assert
        // Version 5 UUIDs have specific version field
        assertEquals(5, uuid.version(), "Generated UUID should be version 5 (name-based)");
    }

    @Test
    @DisplayName("Should accept content parameters (not affect generation)")
    void testGenerateId_WithContentParameters() {
        // Arrange
        DefaultIdGenerator generator = new DefaultIdGenerator("product", 222L);
        Object[] contents = {"content1", "content2", "content3"};

        // Act
        String id1 = generator.generateId();
        String id2 = generator.generateId(contents);

        // Assert
        assertEquals(id1, id2, "Content parameters should not affect UUID generation");
    }
}
