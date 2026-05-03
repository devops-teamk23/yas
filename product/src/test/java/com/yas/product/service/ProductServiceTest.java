package com.yas.product.service;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.repository.*;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MediaService mediaService;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductCategoryRepository productCategoryRepository;
    @Mock
    private ProductImageRepository productImageRepository;
    @Mock
    private ProductOptionRepository productOptionRepository;
    @Mock
    private ProductOptionValueRepository productOptionValueRepository;
    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;
    @Mock
    private ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setSlug("test-product");
        product.setThumbnailMediaId(1L);
    }

    @Test
    void getLatestProducts_CountLessThanOrEqualZero_ReturnEmpty() {
        List<ProductListVm> result = productService.getLatestProducts(0);
        assertThat(result).isEmpty();
    }

    @Test
    void getLatestProducts_ReturnProducts() {
        when(productRepository.getLatestProducts(any(Pageable.class))).thenReturn(List.of(product));
        List<ProductListVm> result = productService.getLatestProducts(1);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).name()).isEqualTo("Test Product");
    }

    @Test
    void getProductById_ProductNotFound_ThrowNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void getProductById_ReturnProductDetail() {
        Brand brand = new Brand();
        brand.setId(2L);
        product.setBrand(brand);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L))
                .thenReturn(new NoFileMediaVm(1L, "caption", "filename", "mediaType", "http://image.url"));

        ProductDetailVm result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Test Product");
        assertThat(result.thumbnailMedia().url()).isEqualTo("http://image.url");
        assertThat(result.brandId()).isEqualTo(2L);
    }

    @Test
    void getProductsByBrand_BrandNotFound_ThrowNotFoundException() {
        when(brandRepository.findBySlug("brand-slug")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductsByBrand("brand-slug"));
    }

    @Test
    void getProductsByBrand_ReturnProducts() {
        Brand brand = new Brand();
        brand.setId(1L);
        when(brandRepository.findBySlug("brand-slug")).thenReturn(Optional.of(brand));
        when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand)).thenReturn(List.of(product));
        when(mediaService.getMedia(1L))
                .thenReturn(new NoFileMediaVm(1L, "caption", "filename", "mediaType", "http://image.url"));

        List<ProductThumbnailVm> result = productService.getProductsByBrand("brand-slug");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).name()).isEqualTo("Test Product");
        assertThat(result.get(0).thumbnailUrl()).isEqualTo("http://image.url");
    }

    @Test
    void getProductsWithFilter_ReturnProductListGetVm() {
        org.springframework.data.domain.Page<Product> productPage = new org.springframework.data.domain.PageImpl<>(
                List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.getProductsWithFilter(any(String.class), any(String.class), any(Pageable.class)))
                .thenReturn(productPage);

        com.yas.product.viewmodel.product.ProductListGetVm result = productService.getProductsWithFilter(0, 10, "Test",
                "Brand");

        assertThat(result).isNotNull();
        assertThat(result.productContent()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void getProductsFromCategory_CategoryNotFound_ThrowException() {
        when(categoryRepository.findBySlug("cat")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductsFromCategory(0, 10, "cat"));
    }

    @Test
    void getProductsFromCategory_ReturnProducts() {
        Category category = new Category();
        when(categoryRepository.findBySlug("cat")).thenReturn(Optional.of(category));

        ProductCategory productCategory = new ProductCategory();
        productCategory.setProduct(product);
        org.springframework.data.domain.Page<ProductCategory> page = new org.springframework.data.domain.PageImpl<>(
                List.of(productCategory), PageRequest.of(0, 10), 1);
        when(productCategoryRepository.findAllByCategory(any(Pageable.class), eq(category))).thenReturn(page);

        when(mediaService.getMedia(1L))
                .thenReturn(new NoFileMediaVm(1L, "caption", "filename", "mediaType", "http://image.url"));

        com.yas.product.viewmodel.product.ProductListGetFromCategoryVm result = productService
                .getProductsFromCategory(0, 10, "cat");

        assertThat(result).isNotNull();
        assertThat(result.productContent()).hasSize(1);
        assertThat(result.productContent().get(0).id()).isEqualTo(1L);
    }

    @Test
    void getListFeaturedProducts_ReturnFeaturedProducts() {
        org.springframework.data.domain.Page<Product> productPage = new org.springframework.data.domain.PageImpl<>(
                List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(productPage);
        when(mediaService.getMedia(1L))
                .thenReturn(new NoFileMediaVm(1L, "caption", "filename", "mediaType", "http://image.url"));

        com.yas.product.viewmodel.product.ProductFeatureGetVm result = productService.getListFeaturedProducts(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.productList()).hasSize(1);
        assertThat(result.totalPage()).isEqualTo(1);
    }

    @Test
    void getProductDetail_Success() {
        when(productRepository.findBySlugAndIsPublishedTrue("slug")).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "caption", "filename", "mediaType", "url"));

        com.yas.product.viewmodel.product.ProductDetailGetVm result = productService.getProductDetail("slug");

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        productService.deleteProduct(1L);
        verify(productRepository).save(product);
        assertThat(product.isPublished()).isFalse();
    }

    @Test
    void getProductsByMultiQuery_Success() {
        org.springframework.data.domain.Page<Product> productPage = new org.springframework.data.domain.PageImpl<>(
                List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(anyString(), anyString(), any(), any(),
                any(Pageable.class))).thenReturn(productPage);
        when(mediaService.getMedia(1L))
                .thenReturn(new NoFileMediaVm(1L, "caption", "filename", "mediaType", "http://image.url"));

        com.yas.product.viewmodel.product.ProductsGetVm result = productService.getProductsByMultiQuery(0, 10, "Test",
                "Cat", 10.0, 100.0);

        assertThat(result).isNotNull();
        assertThat(result.productContent()).hasSize(1);
    }

    @Test
    void exportProducts_Success() {
        Brand brand = new Brand();
        brand.setId(2L);
        brand.setName("Brand 1");
        product.setBrand(brand);
        when(productRepository.getExportingProducts(anyString(), anyString())).thenReturn(List.of(product));

        List<com.yas.product.viewmodel.product.ProductExportingDetailVm> result = productService.exportProducts("test",
                "brand");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).brandId()).isEqualTo(2L);
    }

    @Test
    void getProductSlug_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        com.yas.product.viewmodel.product.ProductSlugGetVm result = productService.getProductSlug(1L);
        assertThat(result.slug()).isEqualTo("test-product");
    }

    @Test
    void getProductSlug_WithParent_ReturnsParentSlug() {
        Product parent = new Product();
        parent.setSlug("parent-slug");
        product.setParent(parent);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getProductSlug(1L);
        assertThat(result.slug()).isEqualTo("parent-slug");
    }

    @Test
    void getProductEsDetailById_Success() {
        Brand brand = new Brand();
        brand.setId(2L);
        brand.setName("Brand");
        product.setBrand(brand);
        product.setProductCategories(new java.util.ArrayList<>());
        product.setAttributeValues(new java.util.ArrayList<>());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getProductEsDetailById(1L);
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.brand()).isEqualTo("Brand");
    }

    @Test
    void getProductEsDetailById_NoBrand_Success() {
        product.setBrand(null);
        product.setProductCategories(new java.util.ArrayList<>());
        product.setAttributeValues(new java.util.ArrayList<>());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getProductEsDetailById(1L);
        assertThat(result.brand()).isNull();
    }

    @Test
    void getRelatedProductsBackoffice_Success() {
        com.yas.product.model.ProductRelated pr = new com.yas.product.model.ProductRelated();
        Product related = new Product();
        related.setId(2L);
        related.setName("Related");
        related.setSlug("related");
        pr.setRelatedProduct(related);
        product.setRelatedProducts(List.of(pr));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getRelatedProductsBackoffice(1L);
        assertThat(result).hasSize(1);
    }

    @Test
    void getProductsForWarehouse_Success() {
        when(productRepository.findProductForWarehouse(anyString(), anyString(), any(), anyString()))
                .thenReturn(List.of(product));
        var result = productService.getProductsForWarehouse("name", "sku", List.of(1L),
                com.yas.product.model.enumeration.FilterExistInWhSelection.ALL);
        assertThat(result).hasSize(1);
    }

    @Test
    void updateProductQuantity_Success() {
        product.setStockQuantity(100L);
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
        productService.updateProductQuantity(List.of(
                new com.yas.product.viewmodel.product.ProductQuantityPostVm(1L, 50L)));
        verify(productRepository).saveAll(any());
    }

    @Test
    void subtractStockQuantity_Success() {
        product.setStockQuantity(100L);
        product.setStockTrackingEnabled(true);
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        productService.subtractStockQuantity(List.of(
                new com.yas.product.viewmodel.product.ProductQuantityPutVm(1L, 30L)));
        verify(productRepository).saveAll(any());
    }

    @Test
    void restoreStockQuantity_Success() {
        product.setStockQuantity(70L);
        product.setStockTrackingEnabled(true);
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        productService.restoreStockQuantity(List.of(
                new com.yas.product.viewmodel.product.ProductQuantityPutVm(1L, 30L)));
        verify(productRepository).saveAll(any());
    }

    @Test
    void getProductByIds_Success() {
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
        var result = productService.getProductByIds(List.of(1L));
        assertThat(result).hasSize(1);
    }

    @Test
    void getProductByCategoryIds_Success() {
        when(productRepository.findByCategoryIdsIn(List.of(1L))).thenReturn(List.of(product));
        var result = productService.getProductByCategoryIds(List.of(1L));
        assertThat(result).hasSize(1);
    }

    @Test
    void getProductByBrandIds_Success() {
        when(productRepository.findByBrandIdsIn(List.of(1L))).thenReturn(List.of(product));
        var result = productService.getProductByBrandIds(List.of(1L));
        assertThat(result).hasSize(1);
    }

    @Test
    void getProductCheckoutList_Success() {
        Brand brand = new Brand();
        brand.setId(2L);
        product.setBrand(brand);
        org.springframework.data.domain.Page<Product> page = new org.springframework.data.domain.PageImpl<>(
                List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.findAllPublishedProductsByIds(any(), any(Pageable.class))).thenReturn(page);
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "cap", "file", "type", "url"));
        var result = productService.getProductCheckoutList(0, 10, List.of(1L));
        assertThat(result.productCheckoutListVms()).hasSize(1);
    }

    @Test
    void getProductVariationsByParentId_NoOptions_ReturnEmpty() {
        product.setHasOptions(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getProductVariationsByParentId(1L);
        assertThat(result).isEmpty();
    }

    @Test
    void getFeaturedProductsById_WithThumbnail_Success() {
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "cap", "file", "type", "url"));
        var result = productService.getFeaturedProductsById(List.of(1L));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).thumbnailUrl()).isEqualTo("url");
    }

    @Test
    void deleteProduct_NotFound_ThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.deleteProduct(99L));
    }

    @Test
    void getProductDetail_NotFound_ThrowException() {
        when(productRepository.findBySlugAndIsPublishedTrue("bad")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductDetail("bad"));
    }

    @Test
    void createProduct_NoVariations_Success() {
        var postVm = new com.yas.product.viewmodel.product.ProductPostVm(
                "New", "new-slug", null, List.of(), "short", "desc", "spec",
                "sku1", "gtin1", 1.0, null, 10.0, 5.0, 3.0, 99.0,
                true, true, false, true, false, "mt", "mk", "md",
                1L, List.of(), List.of(), List.of(), List.of(), List.of(), 1L);
        Product saved = new Product();
        saved.setId(10L);
        saved.setName("New");
        saved.setSlug("new-slug");
        saved.setProductCategories(List.of());
        when(productRepository.findBySlugAndIsPublishedTrue("new-slug")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("sku1")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("gtin1")).thenReturn(Optional.empty());
        when(productRepository.findAllById(any())).thenReturn(List.of());
        when(productRepository.save(any(Product.class))).thenReturn(saved);
        when(productImageRepository.saveAll(any())).thenReturn(List.of());
        when(productCategoryRepository.saveAll(any())).thenReturn(List.of());

        var result = productService.createProduct(postVm);
        assertThat(result.id()).isEqualTo(10L);
    }

    @Test
    void createProduct_LengthLessThanWidth_ThrowException() {
        var postVm = new com.yas.product.viewmodel.product.ProductPostVm(
                "New", "new-slug", null, List.of(), "short", "desc", "spec",
                "sku1", "gtin1", 1.0, null, 5.0, 10.0, 3.0, 99.0,
                true, true, false, true, false, "mt", "mk", "md",
                1L, List.of(), List.of(), List.of(), List.of(), List.of(), 1L);
        assertThrows(com.yas.commonlibrary.exception.BadRequestException.class,
                () -> productService.createProduct(postVm));
    }

    @Test
    void createProduct_DuplicateSlug_ThrowException() {
        var postVm = new com.yas.product.viewmodel.product.ProductPostVm(
                "New", "dup-slug", null, List.of(), "short", "desc", "spec",
                "sku1", "gtin1", 1.0, null, 10.0, 5.0, 3.0, 99.0,
                true, true, false, true, false, "mt", "mk", "md",
                1L, List.of(), List.of(), List.of(), List.of(), List.of(), 1L);
        Product existing = new Product();
        existing.setId(99L);
        when(productRepository.findBySlugAndIsPublishedTrue("dup-slug")).thenReturn(Optional.of(existing));
        assertThrows(com.yas.commonlibrary.exception.DuplicatedException.class,
                () -> productService.createProduct(postVm));
    }

    @Test
    void deleteProduct_WithParent_DeletesCombinations() {
        Product parent = new Product();
        parent.setId(2L);
        product.setParent(parent);
        var combo = com.yas.product.model.ProductOptionCombination.builder().build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(product)).thenReturn(List.of(combo));
        productService.deleteProduct(1L);
        verify(productOptionCombinationRepository).deleteAll(any());
        verify(productRepository).save(product);
    }

    @Test
    void getProductDetail_WithAttributes_Success() {
        var attr = new com.yas.product.model.attribute.ProductAttribute();
        attr.setId(1L);
        attr.setName("Color");
        attr.setProductAttributeGroup(null);
        var attrVal = new com.yas.product.model.attribute.ProductAttributeValue();
        attrVal.setId(1L);
        attrVal.setProductAttribute(attr);
        attrVal.setValue("Red");
        product.setAttributeValues(List.of(attrVal));
        product.setProductCategories(List.of());
        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "c", "f", "t", "url"));
        var result = productService.getProductDetail("test-product");
        assertThat(result).isNotNull();
        assertThat(result.productAttributeGroups()).isNotEmpty();
    }

    @Test
    void getProductDetail_WithImages_Success() {
        var img = com.yas.product.model.ProductImage.builder().imageId(10L).product(product).build();
        product.setProductImages(List.of(img));
        product.setProductCategories(List.of());
        product.setAttributeValues(List.of());
        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "c", "f", "t", "thumb"));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "c", "f", "t", "img"));
        var result = productService.getProductDetail("test-product");
        assertThat(result.productImageMediaUrls()).hasSize(1);
    }

    @Test
    void getRelatedProductsStorefront_Success() {
        Product related = new Product();
        related.setId(2L);
        related.setName("Rel");
        related.setSlug("rel");
        related.setPublished(true);
        related.setThumbnailMediaId(2L);
        var pr = com.yas.product.model.ProductRelated.builder()
                .product(product).relatedProduct(related).build();
        org.springframework.data.domain.Page<com.yas.product.model.ProductRelated> page = new org.springframework.data.domain.PageImpl<>(
                List.of(pr), PageRequest.of(0, 10), 1);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRelatedRepository.findAllByProduct(eq(product), any(Pageable.class))).thenReturn(page);
        when(mediaService.getMedia(2L)).thenReturn(new NoFileMediaVm(2L, "c", "f", "t", "url2"));
        var result = productService.getRelatedProductsStorefront(1L, 0, 10);
        assertThat(result.productContent()).hasSize(1);
    }

    @Test
    void getFeaturedProductsById_WithParentFallback_Success() {
        Product parent = new Product();
        parent.setId(2L);
        parent.setThumbnailMediaId(2L);
        product.setParent(parent);
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "c", "f", "t", ""));
        when(productRepository.findById(2L)).thenReturn(Optional.of(parent));
        when(mediaService.getMedia(2L)).thenReturn(new NoFileMediaVm(2L, "c", "f", "t", "parent_url"));
        var result = productService.getFeaturedProductsById(List.of(1L));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).thumbnailUrl()).isEqualTo("parent_url");
    }

    @Test
    void getProductVariationsByParentId_WithOptions_Success() {
        product.setHasOptions(true);
        Product variation = new Product();
        variation.setId(5L);
        variation.setName("Var");
        variation.setSlug("var");
        variation.setPublished(true);
        variation.setProductImages(List.of());
        product.setProducts(List.of(variation));
        var po = new com.yas.product.model.ProductOption();
        po.setId(10L);
        var combo = com.yas.product.model.ProductOptionCombination.builder()
                .product(variation).productOption(po).value("Red").build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(List.of(combo));
        var result = productService.getProductVariationsByParentId(1L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(5L);
    }

    @Test
    void subtractStockQuantity_NotTracked_NoChange() {
        product.setStockQuantity(100L);
        product.setStockTrackingEnabled(false);
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        productService.subtractStockQuantity(List.of(
                new com.yas.product.viewmodel.product.ProductQuantityPutVm(1L, 30L)));
        assertThat(product.getStockQuantity()).isEqualTo(100L);
    }

    @Test
    void getLatestProducts_EmptyResult_ReturnEmpty() {
        when(productRepository.getLatestProducts(any(Pageable.class))).thenReturn(List.of());
        var result = productService.getLatestProducts(5);
        assertThat(result).isEmpty();
    }

    @Test
    void updateProduct_NotFound_ThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.updateProduct(99L, null));
    }

    @Test
    void updateProduct_Success() {
        // productOptionValues must have entries to avoid BadRequestException,
        // but variations should be empty to hit early return at line 404
        var optVal = new com.yas.product.viewmodel.productoption.ProductOptionValuePutVm(
                10L, "COLOR", 1, List.of("Red"));
        var putVm = new com.yas.product.viewmodel.product.ProductPutVm(
                "Upd", "upd-slug", 10.0, true, true, false, true, false,
                2L, List.of(), "short", "desc", "spec", "sku", "gtin",
                1.0, null, 10.0, 5.0, 3.0, "mt", "mk", "md",
                1L, List.of(), List.of(), List.of(optVal), List.of(), List.of(), 1L);

        product.setProductImages(List.of());
        product.setProducts(List.of());

        var po = new com.yas.product.model.ProductOption();
        po.setId(10L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findBySlugAndIsPublishedTrue("upd-slug")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("sku")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("gtin")).thenReturn(Optional.empty());
        when(brandRepository.findById(2L)).thenReturn(Optional.of(new Brand()));
        when(productCategoryRepository.findAllByProductId(1L)).thenReturn(List.of());
        when(productOptionRepository.findAllByIdIn(List.of(10L))).thenReturn(List.of(po));
        lenient().when(productOptionValueRepository.findAllByProduct(product)).thenReturn(List.of());
        when(productImageRepository.saveAll(any())).thenReturn(List.of());
        when(productRepository.saveAll(any())).thenReturn(List.of());
        when(productRepository.findAllById(any())).thenReturn(List.of());

        productService.updateProduct(1L, putVm);
        // variations is empty, so the method should return early at line 404-405
    }
}
