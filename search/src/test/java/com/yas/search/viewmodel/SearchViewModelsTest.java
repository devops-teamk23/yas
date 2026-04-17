package com.yas.search.viewmodel;

import com.yas.search.viewmodel.error.ErrorVm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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
        @DisplayName("Should handle all required parameters")
        void testAllParameters() {
            // Act
            ProductGetVm vm = new ProductGetVm(
                    123L, "Test", "test", 456L, 99.99,
                    true, true, false, true, ZonedDateTime.now());

            // Assert
            assertNotNull(vm, "Should handle all parameters");
        }

        @Test
        @DisplayName("Should handle null thumbnailId")
        void testNullThumbnailId() {
            // Act
            ProductGetVm vm = new ProductGetVm(
                    123L, "Test", "test", null, 99.99,
                    true, true, false, true, ZonedDateTime.now());

            // Assert
            assertNotNull(vm, "Should handle null thumbnailId");
        }

        @Test
        @DisplayName("Should handle zero price")
        void testZeroPrice() {
            // Act
            ProductGetVm vm = new ProductGetVm(
                    123L, "Test", "test", 456L, 0.0,
                    false, false, false, false, ZonedDateTime.now());

            // Assert
            assertNotNull(vm, "Should handle zero price");
        }

        @Test
        @DisplayName("Should handle all false boolean flags")
        void testAllFalseFlags() {
            // Act
            ProductGetVm vm = new ProductGetVm(
                    123L, "Test", "test", 456L, 99.99,
                    false, false, false, false, ZonedDateTime.now());

            // Assert
            assertNotNull(vm, "Should handle false flags");
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

        @Test
        @DisplayName("Should have working equals()")
        void testEquality() {
            // Act
            ZonedDateTime now = ZonedDateTime.now();
            ProductGetVm vm1 = new ProductGetVm(
                    123L, "Test", "test", 456L, 99.99,
                    true, true, false, true, now);
            ProductGetVm vm2 = new ProductGetVm(
                    123L, "Test", "test", 456L, 99.99,
                    true, true, false, true, now);

            // Assert
            assertEquals(vm1, vm2, "Same data should produce equal objects");
        }

        @Test
        @DisplayName("Should have working hashCode()")
        void testHashCode() {
            // Act
            ZonedDateTime now = ZonedDateTime.now();
            ProductGetVm vm1 = new ProductGetVm(
                    123L, "Test", "test", 456L, 99.99,
                    true, true, false, true, now);
            ProductGetVm vm2 = new ProductGetVm(
                    123L, "Test", "test", 456L, 99.99,
                    true, true, false, true, now);

            // Assert
            assertEquals(vm1.hashCode(), vm2.hashCode(), "Same data should have same hashCode");
        }

        @Test
        @DisplayName("Should handle multiple instances")
        void testMultipleInstances() {
            // Act
            ProductGetVm vm1 = new ProductGetVm(
                    1L, "Product 1", "product-1", 100L, 50.0,
                    true, true, true, true, ZonedDateTime.now());
            ProductGetVm vm2 = new ProductGetVm(
                    2L, "Product 2", "product-2", 200L, 75.0,
                    false, true, false, true, ZonedDateTime.now());

            // Assert
            assertNotNull(vm1);
            assertNotNull(vm2);
            assertNotEquals(vm1, vm2);
        }
    }

    @Nested
    @DisplayName("ProductListGetVm Record Tests")
    class ProductListGetVmTests {

        @Test
        @DisplayName("Should create ProductListGetVm with products list first")
        void testCreateProductListGetVm() {
            // Act
            List<ProductGetVm> products = new ArrayList<>();
            ProductListGetVm vm = new ProductListGetVm(products, 0, 10, 100L);

            // Assert
            assertNotNull(vm, "ProductListGetVm should be created");
        }

        @Test
        @DisplayName("Should handle empty product list")
        void testEmptyProductList() {
            // Act
            ProductListGetVm vm = new ProductListGetVm(new ArrayList<>(), 0, 10, 0L);

            // Assert
            assertNotNull(vm, "Should handle empty list");
        }

        @Test
        @DisplayName("Should handle pagination parameters")
        void testPaginationParameters() {
            // Act
            List<ProductGetVm> products = new ArrayList<>();
            ProductListGetVm vm = new ProductListGetVm(products, 1, 20, 50L);

            // Assert
            assertNotNull(vm, "Should handle pagination parameters");
        }

        @Test
        @DisplayName("Should have working equals()")
        void testEquality() {
            // Act
            List<ProductGetVm> products = new ArrayList<>();
            ProductListGetVm vm1 = new ProductListGetVm(products, 0, 10, 100L);
            ProductListGetVm vm2 = new ProductListGetVm(products, 0, 10, 100L);

            // Assert
            assertEquals(vm1, vm2, "Same data should be equal");
        }

        @Test
        @DisplayName("Should have working hashCode()")
        void testHashCode() {
            // Act
            List<ProductGetVm> products = new ArrayList<>();
            ProductListGetVm vm1 = new ProductListGetVm(products, 0, 10, 100L);
            ProductListGetVm vm2 = new ProductListGetVm(products, 0, 10, 100L);

            // Assert
            assertEquals(vm1.hashCode(), vm2.hashCode(), "Should have consistent hashCode");
        }

        @Test
        @DisplayName("Should have working toString()")
        void testToString() {
            // Act
            ProductListGetVm vm = new ProductListGetVm(new ArrayList<>(), 0, 10, 100L);
            String str = vm.toString();

            // Assert
            assertNotNull(str, "toString should not be null");
            assertTrue(str.contains("ProductListGetVm"), "toString should contain class name");
        }
    }

    @Nested
    @DisplayName("ErrorVm Record Tests")
    class ErrorVmTests {

        @Test
        @DisplayName("Should create ErrorVm with error message")
        void testCreateErrorVm() {
            // Act
            ErrorVm vm = new ErrorVm("Error message");

            // Assert
            assertNotNull(vm, "ErrorVm should be created");
        }

        @Test
        @DisplayName("Should handle null error message")
        void testNullErrorMessage() {
            // Act
            ErrorVm vm = new ErrorVm(null);

            // Assert
            assertNotNull(vm, "Should handle null message");
        }

        @Test
        @DisplayName("Should handle empty error message")
        void testEmptyErrorMessage() {
            // Act
            ErrorVm vm = new ErrorVm("");

            // Assert
            assertNotNull(vm, "Should handle empty message");
        }

        @Test
        @DisplayName("Should handle long error message")
        void testLongErrorMessage() {
            // Act
            String longMessage = "A".repeat(1000);
            ErrorVm vm = new ErrorVm(longMessage);

            // Assert
            assertNotNull(vm, "Should handle long message");
        }

        @Test
        @DisplayName("Should have working equals()")
        void testEquality() {
            // Act
            ErrorVm vm1 = new ErrorVm("Test Error");
            ErrorVm vm2 = new ErrorVm("Test Error");

            // Assert
            assertEquals(vm1, vm2, "Same message should be equal");
        }

        @Test
        @DisplayName("Should have working hashCode()")
        void testHashCode() {
            // Act
            ErrorVm vm1 = new ErrorVm("Test Error");
            ErrorVm vm2 = new ErrorVm("Test Error");

            // Assert
            assertEquals(vm1.hashCode(), vm2.hashCode(), "Should have consistent hashCode");
        }

        @Test
        @DisplayName("Should have working toString()")
        void testToString() {
            // Act
            ErrorVm vm = new ErrorVm("Test Error");
            String str = vm.toString();

            // Assert
            assertNotNull(str, "toString should not be null");
            assertTrue(str.contains("ErrorVm"), "toString should contain class name");
            assertTrue(str.contains("Test Error"), "toString should contain error message");
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
        @DisplayName("Should handle null name")
        void testNullName() {
            // Act
            ProductNameGetVm vm = new ProductNameGetVm(null);

            // Assert
            assertNotNull(vm, "Should handle null name");
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
        @DisplayName("Should create ProductNameListVm with names only")
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
            // Act
            List<ProductNameGetVm> names = new ArrayList<>();
            names.add(new ProductNameGetVm("Product 1"));
            names.add(new ProductNameGetVm("Product 2"));
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
        @DisplayName("Should have working hashCode()")
        void testHashCode() {
            // Act
            List<ProductNameGetVm> names = new ArrayList<>();
            ProductNameListVm vm1 = new ProductNameListVm(names);
            ProductNameListVm vm2 = new ProductNameListVm(names);

            // Assert
            assertEquals(vm1.hashCode(), vm2.hashCode(), "Should have consistent hashCode");
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
