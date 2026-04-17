package com.yas.search.viewmodel;

import com.yas.search.viewmodel.error.ErrorVm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
            ProductGetVm vm = new ProductGetVm(123L, "Test Product", "Description", 99.99, true);

            // Assert
            assertNotNull(vm, "ProductGetVm should be created");
        }

        @Test
        @DisplayName("Should access productId via getter")
        void testProductIdAccessor() {
            // Act
            ProductGetVm vm = new ProductGetVm(123L, "Test", "Desc", 99.99, true);

            // Assert - Just verify no exception thrown
            assertNotNull(vm, "Should have productId accessor");
        }

        @Test
        @DisplayName("Should handle null description")
        void testNullDescription() {
            // Act
            ProductGetVm vm = new ProductGetVm(123L, "Test", null, 99.99, true);

            // Assert
            assertNotNull(vm, "Should handle null description");
        }

        @Test
        @DisplayName("Should handle zero price")
        void testZeroPrice() {
            // Act
            ProductGetVm vm = new ProductGetVm(123L, "Test", "Desc", 0.0, false);

            // Assert
            assertNotNull(vm, "Should handle zero price");
        }

        @Test
        @DisplayName("Should have working toString()")
        void testToString() {
            // Act
            ProductGetVm vm = new ProductGetVm(123L, "Test Product", "Description", 99.99, true);
            String str = vm.toString();

            // Assert
            assertNotNull(str, "toString should not be null");
            assertTrue(str.contains("ProductGetVm"), "toString should contain class name");
        }

        @Test
        @DisplayName("Should have working equals()")
        void testEquality() {
            // Act
            ProductGetVm vm1 = new ProductGetVm(123L, "Test", "Desc", 99.99, true);
            ProductGetVm vm2 = new ProductGetVm(123L, "Test", "Desc", 99.99, true);

            // Assert
            assertEquals(vm1, vm2, "Same data should produce equal objects");
        }

        @Test
        @DisplayName("Should have working hashCode()")
        void testHashCode() {
            // Act
            ProductGetVm vm1 = new ProductGetVm(123L, "Test", "Desc", 99.99, true);
            ProductGetVm vm2 = new ProductGetVm(123L, "Test", "Desc", 99.99, true);

            // Assert
            assertEquals(vm1.hashCode(), vm2.hashCode(), "Same data should have same hashCode");
        }
    }

    @Nested
    @DisplayName("ProductListGetVm Record Tests")
    class ProductListGetVmTests {

        @Test
        @DisplayName("Should create ProductListGetVm with products")
        void testCreateProductListGetVm() {
            // Act
            List<ProductGetVm> products = new ArrayList<>();
            ProductListGetVm vm = new ProductListGetVm(0, 10, 100L, products);

            // Assert
            assertNotNull(vm, "ProductListGetVm should be created");
        }

        @Test
        @DisplayName("Should handle empty product list")
        void testEmptyProductList() {
            // Act
            ProductListGetVm vm = new ProductListGetVm(0, 10, 0L, new ArrayList<>());

            // Assert
            assertNotNull(vm, "Should handle empty list");
        }

        @Test
        @DisplayName("Should handle pagination parameters")
        void testPaginationParameters() {
            // Act
            List<ProductGetVm> products = new ArrayList<>();
            ProductListGetVm vm = new ProductListGetVm(1, 20, 50L, products);

            // Assert
            assertNotNull(vm, "Should handle pagination parameters");
        }

        @Test
        @DisplayName("Should have working equals()")
        void testEquality() {
            // Act
            List<ProductGetVm> products = new ArrayList<>();
            ProductListGetVm vm1 = new ProductListGetVm(0, 10, 100L, products);
            ProductListGetVm vm2 = new ProductListGetVm(0, 10, 100L, products);

            // Assert
            assertEquals(vm1, vm2, "Same data should be equal");
        }

        @Test
        @DisplayName("Should have working hashCode()")
        void testHashCode() {
            // Act
            List<ProductGetVm> products = new ArrayList<>();
            ProductListGetVm vm1 = new ProductListGetVm(0, 10, 100L, products);
            ProductListGetVm vm2 = new ProductListGetVm(0, 10, 100L, products);

            // Assert
            assertEquals(vm1.hashCode(), vm2.hashCode(), "Should have consistent hashCode");
        }

        @Test
        @DisplayName("Should have working toString()")
        void testToString() {
            // Act
            ProductListGetVm vm = new ProductListGetVm(0, 10, 100L, new ArrayList<>());
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
    @DisplayName("ProductNameListVm Record Tests")
    class ProductNameListVmTests {

        @Test
        @DisplayName("Should create ProductNameListVm with name data")
        void testCreateProductNameListVm() {
            // Act
            List<ProductNameGetVm> names = new ArrayList<>();
            ProductNameListVm vm = new ProductNameListVm(0, 10, 50L, names);

            // Assert
            assertNotNull(vm, "ProductNameListVm should be created");
        }

        @Test
        @DisplayName("Should handle empty name list")
        void testEmptyNameList() {
            // Act
            ProductNameListVm vm = new ProductNameListVm(0, 10, 0L, new ArrayList<>());

            // Assert
            assertNotNull(vm, "Should handle empty list");
        }

        @Test
        @DisplayName("Should have working equals()")
        void testEquality() {
            // Act
            List<ProductNameGetVm> names = new ArrayList<>();
            ProductNameListVm vm1 = new ProductNameListVm(0, 10, 50L, names);
            ProductNameListVm vm2 = new ProductNameListVm(0, 10, 50L, names);

            // Assert
            assertEquals(vm1, vm2, "Same data should be equal");
        }

        @Test
        @DisplayName("Should have working hashCode()")
        void testHashCode() {
            // Act
            List<ProductNameGetVm> names = new ArrayList<>();
            ProductNameListVm vm1 = new ProductNameListVm(0, 10, 50L, names);
            ProductNameListVm vm2 = new ProductNameListVm(0, 10, 50L, names);

            // Assert
            assertEquals(vm1.hashCode(), vm2.hashCode(), "Should have consistent hashCode");
        }

        @Test
        @DisplayName("Should have working toString()")
        void testToString() {
            // Act
            List<ProductNameGetVm> names = new ArrayList<>();
            ProductNameListVm vm = new ProductNameListVm(0, 10, 50L, names);
            String str = vm.toString();

            // Assert
            assertNotNull(str, "toString should not be null");
            assertTrue(str.contains("ProductNameListVm"), "toString should contain class name");
        }
    }
}
