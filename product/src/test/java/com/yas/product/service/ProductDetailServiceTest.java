package com.yas.product.service;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductDetailServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MediaService mediaService;
    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    @InjectMocks
    private ProductDetailService productDetailService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setSlug("test-product");
        product.setPublished(true);
        product.setThumbnailMediaId(1L);
        product.setProductCategories(new ArrayList<>());
        product.setAttributeValues(new ArrayList<>());
        product.setProducts(new ArrayList<>());
    }

    @Test
    void getProductDetailById_NotFound_ThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(99L));
    }

    @Test
    void getProductDetailById_UnpublishedProduct_ThrowException() {
        product.setPublished(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(1L));
    }

    @Test
    void getProductDetailById_WithBrand_Success() {
        Brand brand = new Brand();
        brand.setId(2L);
        brand.setName("Brand Name");
        product.setBrand(brand);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "caption", "file", "type", "url"));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getBrandId()).isEqualTo(2L);
        assertThat(result.getBrandName()).isEqualTo("Brand Name");
    }

    @Test
    void getProductDetailById_NoBrand_Success() {
        product.setBrand(null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "caption", "file", "type", "url"));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertThat(result.getBrandId()).isNull();
        assertThat(result.getBrandName()).isNull();
    }

    @Test
    void getProductDetailById_WithCategories_Success() {
        com.yas.product.model.Category cat = new com.yas.product.model.Category();
        cat.setId(5L);
        cat.setName("Cat1");
        ProductCategory pc = new ProductCategory();
        pc.setCategory(cat);
        pc.setProduct(product);
        product.setProductCategories(List.of(pc));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "caption", "file", "type", "url"));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertThat(result.getCategories()).hasSize(1);
    }

    @Test
    void getProductDetailById_WithAttributes_Success() {
        ProductAttribute attr = new ProductAttribute();
        attr.setId(1L);
        attr.setName("Color");
        ProductAttributeValue attrVal = new ProductAttributeValue();
        attrVal.setId(1L);
        attrVal.setProductAttribute(attr);
        attrVal.setValue("Red");
        product.setAttributeValues(List.of(attrVal));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "caption", "file", "type", "url"));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertThat(result.getAttributeValues()).hasSize(1);
    }

    @Test
    void getProductDetailById_NoThumbnail_Success() {
        product.setThumbnailMediaId(null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertThat(result.getThumbnail()).isNull();
    }

    @Test
    void getProductDetailById_WithProductImages_Success() {
        ProductImage img = new ProductImage();
        img.setImageId(10L);
        img.setProduct(product);
        product.setProductImages(List.of(img));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "caption", "file", "type", "thumb_url"));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "caption", "file", "type", "img_url"));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertThat(result.getProductImages()).hasSize(1);
    }
}
