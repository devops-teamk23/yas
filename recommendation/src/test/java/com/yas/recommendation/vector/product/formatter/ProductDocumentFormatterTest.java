package com.yas.recommendation.vector.product.formatter;

import tools.jackson.databind.ObjectMapper;
import com.yas.recommendation.viewmodel.CategoryVm;
import com.yas.recommendation.viewmodel.ProductAttributeValueVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ProductDocumentFormatter class.
 * Tests formatting of product information, attributes, and categories.
 */
@DisplayName("ProductDocumentFormatter Unit Tests")
@ExtendWith(MockitoExtension.class)
class ProductDocumentFormatterTest {

    private ProductDocumentFormatter formatter;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        formatter = new ProductDocumentFormatter();
    }

    @Test
    @DisplayName("Should format basic product information with template substitution")
    void testFormat_BasicProductFormatting() {
        // Arrange
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "iPhone 14 Pro");
        entityMap.put("description", "Latest Apple smartphone");
        entityMap.put("price", "999");
        entityMap.put("attributeValues", null);
        entityMap.put("categories", null);

        String template = "Product: {name}, Description: {description}, Price: {price}";

        // Act
        String result = formatter.format(entityMap, template, objectMapper);

        // Assert
        assertEquals("Product: iPhone 14 Pro, Description: Latest Apple smartphone, Price: 999", result);
    }

    @Test
    @DisplayName("Should format null attribute values as empty brackets")
    void testFormat_NullAttributeValues() {
        // Arrange
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "Product");
        entityMap.put("attributeValues", null);
        entityMap.put("categories", null);

        String template = "Name: {name}, Attributes: {attributeValues}";

        // Act
        String result = formatter.format(entityMap, template, objectMapper);

        // Assert
        assertEquals("Name: Product, Attributes: []", result);
    }

    @Test
    @DisplayName("Should format null categories as empty brackets")
    void testFormat_NullCategories() {
        // Arrange
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "Product");
        entityMap.put("categories", null);
        entityMap.put("attributeValues", null);

        String template = "Name: {name}, Categories: {categories}";

        // Act
        String result = formatter.format(entityMap, template, objectMapper);

        // Assert
        assertEquals("Name: Product, Categories: []", result);
    }

    @Test
    @DisplayName("Should format attribute values with name and value")
    void testFormat_WithAttributeValues() {
        // Arrange
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "Laptop");

        ProductAttributeValueVm attr1 = new ProductAttributeValueVm(1L, "Color", "Silver");
        ProductAttributeValueVm attr2 = new ProductAttributeValueVm(2L, "Storage", "512GB");
        List<Object> attributeValues = new ArrayList<>();
        attributeValues.add(attr1);
        attributeValues.add(attr2);
        entityMap.put("attributeValues", attributeValues);
        entityMap.put("categories", null);

        String template = "Attributes: {attributeValues}";

        when(objectMapper.convertValue(attr1, ProductAttributeValueVm.class)).thenReturn(attr1);
        when(objectMapper.convertValue(attr2, ProductAttributeValueVm.class)).thenReturn(attr2);

        // Act
        String result = formatter.format(entityMap, template, objectMapper);

        // Assert
        assertTrue(result.contains("Color: Silver"));
        assertTrue(result.contains("Storage: 512GB"));
        assertTrue(result.startsWith("Attributes: ["));
        assertTrue(result.endsWith("]"));
    }

    @Test
    @DisplayName("Should format categories with category names")
    void testFormat_WithCategories() {
        // Arrange
        Map<String, Object> entityMap = new HashMap<>();

        CategoryVm cat1 = new CategoryVm(1L, "Electronics");
        CategoryVm cat2 = new CategoryVm(2L, "Phones");
        List<Object> categories = new ArrayList<>();
        categories.add(cat1);
        categories.add(cat2);
        entityMap.put("categories", categories);
        entityMap.put("attributeValues", null);

        String template = "Categories: {categories}";

        when(objectMapper.convertValue(cat1, CategoryVm.class)).thenReturn(cat1);
        when(objectMapper.convertValue(cat2, CategoryVm.class)).thenReturn(cat2);

        // Act
        String result = formatter.format(entityMap, template, objectMapper);

        // Assert
        assertTrue(result.contains("Electronics"));
        assertTrue(result.contains("Phones"));
        assertTrue(result.startsWith("Categories: ["));
        assertTrue(result.endsWith("]"));
    }

    @Test
    @DisplayName("Should remove HTML tags from formatted output")
    void testFormat_RemoveHtmlTags() {
        // Arrange
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "Product <b>Name</b>");
        entityMap.put("description", "<p>Description with <strong>HTML</strong></p>");
        entityMap.put("attributeValues", null);
        entityMap.put("categories", null);

        String template = "Name: {name}, Desc: {description}";

        // Act
        String result = formatter.format(entityMap, template, objectMapper);

        // Assert
        assertFalse(result.contains("<"));
        assertFalse(result.contains(">"));
        assertTrue(result.contains("Product Name"));
        assertTrue(result.contains("Description with HTML"));
    }

    @Test
    @DisplayName("Should handle empty attribute values list")
    void testFormat_EmptyAttributeValuesList() {
        // Arrange
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "Product");
        entityMap.put("attributeValues", new ArrayList<>());
        entityMap.put("categories", null);

        String template = "Name: {name}, Attributes: {attributeValues}";

        // Act
        String result = formatter.format(entityMap, template, objectMapper);

        // Assert
        assertEquals("Name: Product, Attributes: []", result);
    }

    @Test
    @DisplayName("Should handle empty categories list")
    void testFormat_EmptyCategoriesList() {
        // Arrange
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("categories", new ArrayList<>());
        entityMap.put("attributeValues", null);

        String template = "Categories: {categories}";

        // Act
        String result = formatter.format(entityMap, template, objectMapper);

        // Assert
        assertEquals("Categories: []", result);
    }

    @Test
    @DisplayName("Should format multiple attributes in sequence")
    void testFormat_MultipleAttributes() {
        // Arrange
        Map<String, Object> entityMap = new HashMap<>();

        ProductAttributeValueVm attr1 = new ProductAttributeValueVm(1L, "CPU", "Intel i9");
        ProductAttributeValueVm attr2 = new ProductAttributeValueVm(2L, "RAM", "32GB");
        ProductAttributeValueVm attr3 = new ProductAttributeValueVm(3L, "GPU", "RTX 4090");
        List<Object> attributeValues = new ArrayList<>();
        attributeValues.add(attr1);
        attributeValues.add(attr2);
        attributeValues.add(attr3);
        entityMap.put("attributeValues", attributeValues);
        entityMap.put("categories", null);

        String template = "Specs: {attributeValues}";

        when(objectMapper.convertValue(attr1, ProductAttributeValueVm.class)).thenReturn(attr1);
        when(objectMapper.convertValue(attr2, ProductAttributeValueVm.class)).thenReturn(attr2);
        when(objectMapper.convertValue(attr3, ProductAttributeValueVm.class)).thenReturn(attr3);

        // Act
        String result = formatter.format(entityMap, template, objectMapper);

        // Assert
        assertTrue(result.contains("CPU: Intel i9"));
        assertTrue(result.contains("RAM: 32GB"));
        assertTrue(result.contains("GPU: RTX 4090"));
    }

    @Test
    @DisplayName("Should handle attributes with special characters")
    void testFormat_AttributesWithSpecialCharacters() {
        // Arrange
        Map<String, Object> entityMap = new HashMap<>();

        ProductAttributeValueVm attr = new ProductAttributeValueVm(1L, "Description", "100% Pure & Organic");
        List<Object> attributeValues = List.of(attr);
        entityMap.put("attributeValues", attributeValues);
        entityMap.put("categories", null);

        String template = "Attr: {attributeValues}";

        when(objectMapper.convertValue(attr, ProductAttributeValueVm.class)).thenReturn(attr);

        // Act
        String result = formatter.format(entityMap, template, objectMapper);

        // Assert
        assertTrue(result.contains("100% Pure & Organic"));
    }

    @Test
    @DisplayName("Should handle complex template with multiple placeholders")
    void testFormat_ComplexTemplate() {
        // Arrange
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("productName", "MacBook Pro");
        entityMap.put("price", "$2499");
        entityMap.put("stock", "In Stock");
        entityMap.put("attributeValues", null);
        entityMap.put("categories", null);

        String template = "Product: {productName} | Price: {price} | Status: {stock}";

        // Act
        String result = formatter.format(entityMap, template, objectMapper);

        // Assert
        assertEquals("Product: MacBook Pro | Price: $2499 | Status: In Stock", result);
    }

    @Test
    @DisplayName("Should handle attributes and categories together")
    void testFormat_WithBothAttributesAndCategories() {
        // Arrange
        Map<String, Object> entityMap = new HashMap<>();

        ProductAttributeValueVm attr = new ProductAttributeValueVm(1L, "Color", "Black");
        CategoryVm category = new CategoryVm(1L, "Electronics");
        
        List<Object> attributeValues = List.of(attr);
        List<Object> categories = List.of(category);
        
        entityMap.put("attributeValues", attributeValues);
        entityMap.put("categories", categories);

        String template = "Attributes: {attributeValues}, Categories: {categories}";

        when(objectMapper.convertValue(attr, ProductAttributeValueVm.class)).thenReturn(attr);
        when(objectMapper.convertValue(category, CategoryVm.class)).thenReturn(category);

        // Act
        String result = formatter.format(entityMap, template, objectMapper);

        // Assert
        assertTrue(result.contains("Color: Black"));
        assertTrue(result.contains("Electronics"));
    }

    @Test
    @DisplayName("Should escape HTML entities in content")
    void testFormat_HtmlEntitiesRemoval() {
        // Arrange
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("note", "Price &lt; $100 &amp; Quality &gt; 5 stars");
        entityMap.put("attributeValues", null);
        entityMap.put("categories", null);

        String template = "Note: {note}";

        // Act
        String result = formatter.format(entityMap, template, objectMapper);

        // Assert
        assertFalse(result.contains("<"));
        assertFalse(result.contains(">"));
    }
}
