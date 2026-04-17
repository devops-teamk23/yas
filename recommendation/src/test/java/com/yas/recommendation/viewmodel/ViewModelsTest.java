package com.yas.recommendation.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for viewmodel record classes.
 * Tests verify record behavior, serialization, and data handling.
 */
@DisplayName("ViewModels Tests")
class ViewModelsTest {

    @Nested
    @DisplayName("ImageVm Record Tests")
    class ImageVmTests {

        @Test
        @DisplayName("Should create ImageVm with id and url")
        void testCreateImageVm() {
            // Act
            ImageVm image = new ImageVm(123L, "http://example.com/image.jpg");

            // Assert
            assertNotNull(image, "ImageVm should not be null");
            assertEquals(123L, image.id(), "Image ID should match");
            assertEquals("http://example.com/image.jpg", image.url(), "Image URL should match");
        }

        @Test
        @DisplayName("Should handle ImageVm with null URL")
        void testImageVmWithNullUrl() {
            // Act
            ImageVm image = new ImageVm(456L, null);

            // Assert
            assertNotNull(image, "ImageVm should not be null");
            assertEquals(456L, image.id(), "Image ID should match");
            assertNull(image.url(), "Image URL should be null");
        }

        @Test
        @DisplayName("Should handle ImageVm with empty URL")
        void testImageVmWithEmptyUrl() {
            // Act
            ImageVm image = new ImageVm(789L, "");

            // Assert
            assertNotNull(image, "ImageVm should not be null");
            assertEquals(789L, image.id(), "Image ID should match");
            assertEquals("", image.url(), "Image URL should be empty");
        }

        @Test
        @DisplayName("Should verify ImageVm record equality")
        void testImageVmEquality() {
            // Act
            ImageVm image1 = new ImageVm(100L, "http://example.com/img1.jpg");
            ImageVm image2 = new ImageVm(100L, "http://example.com/img1.jpg");

            // Assert
            assertEquals(image1, image2, "ImageVm records with same values should be equal");
        }

        @Test
        @DisplayName("Should handle ImageVm with long URL")
        void testImageVmWithLongUrl() {
            // Arrange
            long imageId = 500L;
            String longUrl = "http://cdn.example.com/path/to/very/long/image/filename/" + "a".repeat(100) + ".jpg";

            // Act
            ImageVm image = new ImageVm(imageId, longUrl);

            // Assert
            assertEquals(imageId, image.id(), "Image ID should match");
            assertEquals(longUrl, image.url(), "Long URL should be stored correctly");
        }
    }

    @Nested
    @DisplayName("RelatedProductVm Record Tests")
    class RelatedProductVmTests {

        @Test
        @DisplayName("Should create RelatedProductVm with all fields")
        void testCreateRelatedProductVm() {
            // Act
            RelatedProductVm relatedProduct = new RelatedProductVm(
                    123L, "Related Product", "related-product", 99.99, true, true
            );

            // Assert
            assertNotNull(relatedProduct, "RelatedProductVm should not be null");
            assertEquals(123L, relatedProduct.id(), "Product ID should match");
            assertEquals("Related Product", relatedProduct.name(), "Product name should match");
            assertEquals("related-product", relatedProduct.slug(), "Product slug should match");
            assertEquals(99.99, relatedProduct.price(), "Product price should match");
            assertEquals(true, relatedProduct.isPublished(), "Published flag should match");
            assertEquals(true, relatedProduct.isFeatured(), "Featured flag should match");
        }

        @Test
        @DisplayName("Should handle RelatedProductVm with false flags")
        void testRelatedProductVmWithFalseFlags() {
            // Act
            RelatedProductVm relatedProduct = new RelatedProductVm(
                    456L, "Unpublished Product", "unpub-product", 50.00, false, false
            );

            // Assert
            assertEquals(false, relatedProduct.isPublished(), "Published flag should be false");
            assertEquals(false, relatedProduct.isFeatured(), "Featured flag should be false");
        }

        @Test
        @DisplayName("Should handle RelatedProductVm with zero price")
        void testRelatedProductVmWithZeroPrice() {
            // Act
            RelatedProductVm relatedProduct = new RelatedProductVm(
                    789L, "Free Product", "free-product", 0.0, true, false
            );

            // Assert
            assertEquals(0.0, relatedProduct.price(), "Price should be zero");
        }

        @Test
        @DisplayName("Should handle RelatedProductVm with high price")
        void testRelatedProductVmWithHighPrice() {
            // Act
            RelatedProductVm relatedProduct = new RelatedProductVm(
                    999L, "Premium Product", "premium-product", 9999.99, true, true
            );

            // Assert
            assertEquals(9999.99, relatedProduct.price(), "High price should be handled");
        }

        @Test
        @DisplayName("Should verify RelatedProductVm record equality")
        void testRelatedProductVmEquality() {
            // Act
            RelatedProductVm product1 = new RelatedProductVm(
                    100L, "Product A", "product-a", 100.0, true, false
            );
            RelatedProductVm product2 = new RelatedProductVm(
                    100L, "Product A", "product-a", 100.0, true, false
            );

            // Assert
            assertEquals(product1, product2, "RelatedProductVm records with same values should be equal");
        }
    }

    @Nested
    @DisplayName("ProductDetailVm Record Tests")
    class ProductDetailVmTests {

        @Test
        @DisplayName("Should create ProductDetailVm with all fields")
        void testCreateProductDetailVmFull() {
            // Arrange
            List<ImageVm> images = new ArrayList<>();
            images.add(new ImageVm(1L, "http://example.com/img1.jpg"));
            ImageVm thumbnail = new ImageVm(2L, "http://example.com/thumbnail.jpg");

            // Act
            ProductDetailVm product = new ProductDetailVm(
                    123L, "Test Product", "Short desc", "Full description", "Specifications",
                    "SKU-123", "GTIN-456", "test-product", true, true, true, true,
                    true, 100.0, 1L, new ArrayList<>(), "Meta Title", "Meta Keywords", 
                    "Meta Description", 1L, "Brand Name", new ArrayList<>(), new ArrayList<>(), 
                    thumbnail, images
            );

            // Assert
            assertNotNull(product, "ProductDetailVm should not be null");
            assertEquals(123L, product.id(), "Product ID should match");
            assertEquals("Test Product", product.name(), "Product name should match");
            assertEquals(100.0, product.price(), "Product price should match");
            assertEquals(1, product.productImages().size(), "Images list should have 1 element");
        }

        @Test
        @DisplayName("Should create ProductDetailVm with minimal fields")
        void testCreateProductDetailVmMinimal() {
            // Arrange
            List<ImageVm> emptyImages = new ArrayList<>();

            // Act
            ProductDetailVm product = new ProductDetailVm(
                    456L, "Minimal Product", null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null, null, null,
                    null, null, new ArrayList<>(), new ArrayList<>(), null, emptyImages
            );

            // Assert
            assertNotNull(product, "ProductDetailVm should not be null");
            assertEquals(456L, product.id(), "Product ID should match");
            assertEquals("Minimal Product", product.name(), "Product name should match");
            assertEquals(0, product.productImages().size(), "Images list should be empty");
        }

        @Test
        @DisplayName("Should handle ProductDetailVm with all boolean flags true")
        void testProductDetailVmWithAllTrueFlags() {
            // Arrange - Act
            ProductDetailVm product = new ProductDetailVm(
                    789L, "Enabled Product", null, null, null, null, null, null,
                    true, true, true, true, true, null, null, null, null, null, null,
                    null, null, new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>()
            );

            // Assert
            assertEquals(true, product.isAllowedToOrder(), "isAllowedToOrder should be true");
            assertEquals(true, product.isPublished(), "isPublished should be true");
            assertEquals(true, product.isFeatured(), "isFeatured should be true");
            assertEquals(true, product.isVisible(), "isVisible should be true");
        }

        @Test
        @DisplayName("Should handle ProductDetailVm with all boolean flags false")
        void testProductDetailVmWithAllFalseFlags() {
            // Arrange - Act
            ProductDetailVm product = new ProductDetailVm(
                    999L, "Disabled Product", null, null, null, null, null, null,
                    false, false, false, false, false, null, null, null, null, null, null,
                    null, null, new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>()
            );

            // Assert
            assertEquals(false, product.isAllowedToOrder(), "isAllowedToOrder should be false");
            assertEquals(false, product.isPublished(), "isPublished should be false");
            assertEquals(false, product.isFeatured(), "isFeatured should be false");
            assertEquals(false, product.isVisible(), "isVisible should be false");
        }

        @Test
        @DisplayName("Should handle ProductDetailVm with multiple images")
        void testProductDetailVmWithMultipleImages() {
            // Arrange
            List<ImageVm> images = new ArrayList<>();
            images.add(new ImageVm(1L, "http://example.com/img1.jpg"));
            images.add(new ImageVm(2L, "http://example.com/img2.jpg"));
            images.add(new ImageVm(3L, "http://example.com/img3.jpg"));

            // Act
            ProductDetailVm product = new ProductDetailVm(
                    111L, "Multi-Image Product", null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null, null, null,
                    null, null, new ArrayList<>(), new ArrayList<>(), null, images
            );

            // Assert
            assertEquals(3, product.productImages().size(), "Product should have 3 images");
            assertEquals("http://example.com/img1.jpg", product.productImages().get(0).url(), 
                    "First image URL should match");
            assertEquals("http://example.com/img3.jpg", product.productImages().get(2).url(), 
                    "Third image URL should match");
        }

        @Test
        @DisplayName("Should handle ProductDetailVm with various price values")
        void testProductDetailVmWithVariousPrices() {
            // Act & Assert
            // Test zero price
            ProductDetailVm zeroPriceProduct = new ProductDetailVm(
                    200L, "Free Product", null, null, null, null, null, null,
                    null, null, null, null, null, 0.0, null, null, null, null, null,
                    null, null, new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>()
            );
            assertEquals(0.0, zeroPriceProduct.price(), "Zero price should be handled");

            // Test high price
            ProductDetailVm highPriceProduct = new ProductDetailVm(
                    201L, "Expensive Product", null, null, null, null, null, null,
                    null, null, null, null, null, 99999.99, null, null, null, null, null,
                    null, null, new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>()
            );
            assertEquals(99999.99, highPriceProduct.price(), "High price should be handled");

            // Test decimal price
            ProductDetailVm decimalProduct = new ProductDetailVm(
                    202L, "Decimal Product", null, null, null, null, null, null,
                    null, null, null, null, null, 123.456, null, null, null, null, null,
                    null, null, new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>()
            );
            assertEquals(123.456, decimalProduct.price(), "Decimal price should be handled");
        }

        @Test
        @DisplayName("Should verify ProductDetailVm record equality")
        void testProductDetailVmEquality() {
            // Arrange
            List<ImageVm> images = new ArrayList<>();

            // Act
            ProductDetailVm product1 = new ProductDetailVm(
                    300L, "Same Product", null, null, null, null, null, null,
                    null, null, null, null, null, 100.0, null, null, null, null, null,
                    null, null, new ArrayList<>(), new ArrayList<>(), null, images
            );
            ProductDetailVm product2 = new ProductDetailVm(
                    300L, "Same Product", null, null, null, null, null, null,
                    null, null, null, null, null, 100.0, null, null, null, null, null,
                    null, null, new ArrayList<>(), new ArrayList<>(), null, images
            );

            // Assert
            assertEquals(product1, product2, "ProductDetailVm records with same values should be equal");
        }
    }

    @Nested
    @DisplayName("ViewModels Edge Cases")
    class ViewModelsEdgeCases {

        @Test
        @DisplayName("Should handle ProductDetailVm with very long text fields")
        void testProductDetailVmWithLongText() {
            // Arrange
            String longText = "a".repeat(1000);

            // Act
            ProductDetailVm product = new ProductDetailVm(
                    400L, longText, longText, longText, longText, longText, longText, longText,
                    null, null, null, null, null, null, null, null, null, null, null,
                    null, null, new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>()
            );

            // Assert
            assertEquals(longText, product.name(), "Should handle long text");
            assertEquals(longText, product.description(), "Should handle long description");
        }

        @Test
        @DisplayName("Should handle ProductDetailVm with special characters in text")
        void testProductDetailVmWithSpecialCharacters() {
            // Arrange
            String specialText = "Product @#$%^&*() <script>alert('xss')</script> テスト";

            // Act
            ProductDetailVm product = new ProductDetailVm(
                    500L, specialText, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null, null, null,
                    null, null, new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>()
            );

            // Assert
            assertEquals(specialText, product.name(), "Should handle special characters");
        }

        @Test
        @DisplayName("Should handle ProductDetailVm with very large product ID")
        void testProductDetailVmWithLargeId() {
            // Act
            ProductDetailVm product = new ProductDetailVm(
                    Long.MAX_VALUE, "Large ID Product", null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null, null, null,
                    null, null, new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>()
            );

            // Assert
            assertEquals(Long.MAX_VALUE, product.id(), "Should handle very large ID");
        }
    }
}
