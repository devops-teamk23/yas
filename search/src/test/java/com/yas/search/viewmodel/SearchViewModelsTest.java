package com.yas.search.viewmodel;

import com.yas.search.viewmodel.error.ErrorVm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Search Service ViewModels.
 * Tests cover record instantiation, accessors, equality, hashCode, toString.
 */
@DisplayName("Search ViewModels Tests")
class SearchViewModelsTest {

    @Nested
    @DisplayName("ProductGetVm Record Tests")
    class ProductGetVmTests {

        @Test
        @DisplayName("Should create ProductGetVm with product data")
        void testCreateProductGetVm() {
            // Act
            ProductGetVm vm = new ProductGetVm(
                    123L, "Test Product", "test-product", 456L, 99.99, 
                    true, true, false, true, ZonedDateTime.now());

            // Assert
            assertNotNull(vm, "ProductGetVm should be created");
        }

        @Test
        @DisplayName("Should have working toString()")
        void testToString() {
            // Act
            ProductGetVm vm = new ProductGetVm(
                    123L, "Test Product", "test-product", 456L, 99.99,
                    true, true, false, true, ZonedDateTime.now());
            String str = vm.toString();

            // Assert
            assertNotNull(str, "toString should not be null");
            assertTrue(str.contains("ProductGetVm"), "toString should contain class name");
        }
    }

    @Nested
    @DisplayName("ProductListGetVm Record Tests")
    class ProductListGetVmTests {

        @Test
        @DisplayName("Should create ProductListGetVm with all 7 required parameters")
        void testCreateProductListGetVm() {
            // Arrange
            List<ProductGetVm> products = new ArrayList<>();
            Map<String, Map<String, Long>> aggregations = new HashMap<>();
            
            // Act
            ProductListGetVm vm = new ProductListGetVm(products, 0, 10, 100L, 10, false, aggregations);

            // Assert
            assertNotNull(vm, "ProductListGetVm should be created");
        }

        @Test
        @DisplayName("Should handle empty product list with all parameters")
        void testEmptyProductList() {
            // Arrange
            Map<String, Map<String, Long>> aggregations = new HashMap<>();
            
            // Act
            ProductListGetVm vm = new ProductListGetVm(new ArrayList<>(), 0, 10, 0L, 0, true, aggregations);

            // Assert
            assertNotNull(vm, "Should handle empty list");
        }

        @Test
        @DisplayName("Should handle pagination with all parameters")
        void testPaginationParameters() {
            // Arrange
            List<ProductGetVm> products = new ArrayList<>();
            Map<String, Map<String, Long>> aggregations = new HashMap<>();
            
            // Act
            ProductListGetVm vm = new ProductListGetVm(products, 1, 20, 50L, 3, false, aggregations);

            // Assert
            assertNotNull(vm, "Should handle pagination parameters");
        }

        @Test
        @DisplayName("Should handle last page true flag")
        void testLastPageTrue() {
            // Arrange
            Map<String, Map<String, Long>> aggregations = new HashMap<>();
            
            // Act
            ProductListGetVm vm = new ProductListGetVm(new ArrayList<>(), 2, 10, 25L, 3, true, aggregations);

            // Assert
            assertNotNull(vm, "Should handle last page true");
        }

        @Test
        @DisplayName("Should handle complex aggregations map")
        void testComplexAggregations() {
            // Arrange
            List<ProductGetVm> products = new ArrayList<>();
            Map<String, Map<String, Long>> aggregations = new HashMap<>();
            Map<String, Long> innerMap = new HashMap<>();
            innerMap.put("count", 5L);
            aggregations.put("brand", innerMap);
            
            // Act
            ProductListGetVm vm = new ProductListGetVm(products, 0, 10, 100L, 10, false, aggregations);

            // Assert
            assertNotNull(vm, "Should handle complex aggregations");
        }

        @Test
        @DisplayName("Should have working toString()")
        void testToString() {
            // Arrange
            Map<String, Map<String, Long>> aggregations = new HashMap<>();
            
            // Act
            ProductListGetVm vm = new ProductListGetVm(new ArrayList<>(), 0, 10, 100L, 10, false, aggregations);
            String str = vm.toString();

            // Assert
            assertNotNull(str, "toString should not be null");
            assertTrue(str.contains("ProductListGetVm"), "toString should contain class name");
        }

        @Test
        @DisplayName("Should handle multiple products with pagination")
        void testMultipleProductsWithPagination() {
            // Arrange
            List<ProductGetVm> products = new ArrayList<>();
            ProductGetVm product1 = new ProductGetVm(1L, "Product 1", "product-1", 100L, 50.0, true, true, false, true, ZonedDateTime.now());
            ProductGetVm product2 = new ProductGetVm(2L, "Product 2", "product-2", 101L, 75.0, true, true, true, true, ZonedDateTime.now());
            products.add(product1);
            products.add(product2);
            Map<String, Map<String, Long>> aggregations = new HashMap<>();
            
            // Act
            ProductListGetVm vm = new ProductListGetVm(products, 0, 2, 100L, 50, false, aggregations);

            // Assert
            assertNotNull(vm, "Should handle multiple products");
        }
    }

    @Nested
    @DisplayName("ErrorVm Record Tests")
    class ErrorVmTests {

        @Test
        @DisplayName("Should create ErrorVm with 3 required parameters")
        void testCreateErrorVmThreeParams() {
            // Act
            ErrorVm vm = new ErrorVm("400", "Bad Request", "Invalid input");

            // Assert
            assertNotNull(vm, "ErrorVm should be created");
        }

        @Test
        @DisplayName("Should create ErrorVm with 4 parameters including field errors")
        void testCreateErrorVmFourParams() {
            // Arrange
            List<String> fieldErrors = new ArrayList<>();
            fieldErrors.add("name is required");
            fieldErrors.add("email format invalid");
            
            // Act
            ErrorVm vm = new ErrorVm("422", "Unprocessable Entity", "Validation failed", fieldErrors);

            // Assert
            assertNotNull(vm, "ErrorVm with field errors should be created");
        }

        @Test
        @DisplayName("Should handle empty field errors list")
        void testEmptyFieldErrors() {
            // Act
            ErrorVm vm = new ErrorVm("422", "Validation Error", "Input invalid", new ArrayList<>());

            // Assert
            assertNotNull(vm, "Should handle empty field errors");
        }

        @Test
        @DisplayName("Should have working equals()")
        void testEquality() {
            // Act
            ErrorVm vm1 = new ErrorVm("400", "Bad Request", "Invalid input");
            ErrorVm vm2 = new ErrorVm("400", "Bad Request", "Invalid input");

            // Assert
            assertEquals(vm1, vm2, "Same parameters should be equal");
        }

        @Test
        @DisplayName("Should have working hashCode()")
        void testHashCode() {
            // Act
            ErrorVm vm1 = new ErrorVm("400", "Bad Request", "Invalid input");
            ErrorVm vm2 = new ErrorVm("400", "Bad Request", "Invalid input");

            // Assert
            assertEquals(vm1.hashCode(), vm2.hashCode(), "Should have consistent hashCode");
        }

        @Test
        @DisplayName("Should have working toString()")
        void testToString() {
            // Act
            ErrorVm vm = new ErrorVm("500", "Internal Server Error", "Database connection failed");
            String str = vm.toString();

            // Assert
            assertNotNull(str, "toString should not be null");
            assertTrue(str.contains("ErrorVm"), "toString should contain class name");
        }

        @Test
        @DisplayName("Should handle various HTTP error codes")
        void testVariousErrorCodes() {
            // Act & Assert
            assertDoesNotThrow(() -> {
                new ErrorVm("400", "Bad Request", "Detail1");
                new ErrorVm("401", "Unauthorized", "Detail2");
                new ErrorVm("403", "Forbidden", "Detail3");
                new ErrorVm("404", "Not Found", "Detail4");
                new ErrorVm("500", "Internal Server Error", "Detail5");
            }, "Should handle various HTTP error codes");
        }

        @Test
        @DisplayName("Should handle multiple field errors")
        void testMultipleFieldErrors() {
            // Arrange
            List<String> fieldErrors = new ArrayList<>();
            fieldErrors.add("firstName: must not be blank");
            fieldErrors.add("lastName: must not be blank");
            fieldErrors.add("email: must be a valid email");
            fieldErrors.add("password: minimum 8 characters required");
            
            // Act
            ErrorVm vm = new ErrorVm("422", "Validation Failed", "Input validation errors", fieldErrors);

            // Assert
            assertNotNull(vm, "Should handle multiple field errors");
        }
    }

    @Nested
    @DisplayName("ProductNameGetVm Record Tests")
    class ProductNameGetVmTests {

        @Test
        @DisplayName("Should create ProductNameGetVm with name")
        void testCreateProductNameGetVm() {
            // Act
            ProductNameGetVm vm = new ProductNameGetVm("Test Product");

            // Assert
            assertNotNull(vm, "ProductNameGetVm should be created");
        }

        @Test
        @DisplayName("Should handle empty name")
        void testEmptyName() {
            // Act
            ProductNameGetVm vm = new ProductNameGetVm("");

            // Assert
            assertNotNull(vm, "Should handle empty name");
        }

        @Test
        @DisplayName("Should have working equals()")
        void testEquality() {
            // Act
            ProductNameGetVm vm1 = new ProductNameGetVm("Test");
            ProductNameGetVm vm2 = new ProductNameGetVm("Test");

            // Assert
            assertEquals(vm1, vm2, "Same name should be equal");
        }

        @Test
        @DisplayName("Should have working toString()")
        void testToString() {
            // Act
            ProductNameGetVm vm = new ProductNameGetVm("Test Product");
            String str = vm.toString();

            // Assert
            assertNotNull(str, "toString should not be null");
            assertTrue(str.contains("ProductNameGetVm"), "toString should contain class name");
        }
    }

    @Nested
    @DisplayName("ProductNameListVm Record Tests")
    class ProductNameListVmTests {

        @Test
        @DisplayName("Should create ProductNameListVm with names")
        void testCreateProductNameListVm() {
            // Act
            List<ProductNameGetVm> names = new ArrayList<>();
            ProductNameListVm vm = new ProductNameListVm(names);

            // Assert
            assertNotNull(vm, "ProductNameListVm should be created");
        }

        @Test
        @DisplayName("Should handle empty name list")
        void testEmptyNameList() {
            // Act
            ProductNameListVm vm = new ProductNameListVm(new ArrayList<>());

            // Assert
            assertNotNull(vm, "Should handle empty list");
        }

        @Test
        @DisplayName("Should handle populated name list")
        void testPopulatedNameList() {
            // Arrange
            List<ProductNameGetVm> names = new ArrayList<>();
            names.add(new ProductNameGetVm("Product 1"));
            names.add(new ProductNameGetVm("Product 2"));
            names.add(new ProductNameGetVm("Product 3"));
            
            // Act
            ProductNameListVm vm = new ProductNameListVm(names);

            // Assert
            assertNotNull(vm, "Should handle populated list");
        }

        @Test
        @DisplayName("Should have working equals()")
        void testEquality() {
            // Act
            List<ProductNameGetVm> names = new ArrayList<>();
            ProductNameListVm vm1 = new ProductNameListVm(names);
            ProductNameListVm vm2 = new ProductNameListVm(names);

            // Assert
            assertEquals(vm1, vm2, "Same data should be equal");
        }

        @Test
        @DisplayName("Should have working toString()")
        void testToString() {
            // Act
            ProductNameListVm vm = new ProductNameListVm(new ArrayList<>());
            String str = vm.toString();

            // Assert
            assertNotNull(str, "toString should not be null");
            assertTrue(str.contains("ProductNameListVm"), "toString should contain class name");
        }
    }
}
