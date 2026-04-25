package com.yas.product.viewmodel;

import com.yas.product.model.Product;
import com.yas.product.model.Brand;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductCheckoutListVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ProductViewModelTest {

    @Test
    void testProductListVmFromModel() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product Name");
        product.setSlug("product-name");
        product.setAllowedToOrder(true);
        product.setPublished(true);
        product.setFeatured(false);
        product.setVisibleIndividually(true);
        product.setPrice(100.0);
        product.setTaxClassId(1L);

        ProductListVm vm = ProductListVm.fromModel(product);

        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.name()).isEqualTo("Product Name");
        assertThat(vm.slug()).isEqualTo("product-name");
        assertThat(vm.isAllowedToOrder()).isTrue();
        assertThat(vm.isPublished()).isTrue();
        assertThat(vm.isFeatured()).isFalse();
        assertThat(vm.isVisibleIndividually()).isTrue();
        assertThat(vm.price()).isEqualTo(100.0);
        assertThat(vm.taxClassId()).isEqualTo(1L);
    }
    
    @Test
    void testProductCheckoutListVmFromModel() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product Name");
        product.setPrice(100.0);
        Brand brand = new Brand();
        brand.setId(2L);
        product.setBrand(brand);

        ProductCheckoutListVm vm = ProductCheckoutListVm.fromModel(product);

        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.name()).isEqualTo("Product Name");
        assertThat(vm.price()).isEqualTo(100.0);
    }

    @Test
    void testProductDetailVmBuilder() {
        ProductDetailVm vm = ProductDetailVm.builder()
                .id(1L)
                .name("Test Name")
                .price(200.0)
                .isAllowedToOrder(true)
                .build();
        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.name()).isEqualTo("Test Name");
        assertThat(vm.price()).isEqualTo(200.0);
        assertThat(vm.isAllowedToOrder()).isTrue();
    }

    @Test
    void testProductDetailInfoVm() {
        ProductDetailInfoVm vm = new ProductDetailInfoVm(
                1L, "Test Name", "short", "desc", "spec", "sku", "gtin", "slug",
                true, true, false, true, true, 100.0, 2L,
                null, "title", "keyword", "metaDesc", 1L,
                "Brand", null, null, null, null
        );
        assertThat(vm.getId()).isEqualTo(1L);
        assertThat(vm.getName()).isEqualTo("Test Name");
    }

    @Test
    void testProductGetDetailVmFromModel() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Name");
        product.setPrice(100.0);
        
        com.yas.product.viewmodel.product.ProductGetDetailVm vm = com.yas.product.viewmodel.product.ProductGetDetailVm.fromModel(product);
        assertThat(vm.id()).isEqualTo(1L);
    }

    @Test
    void testProductListGetVm() {
        com.yas.product.viewmodel.product.ProductListGetVm vm = new com.yas.product.viewmodel.product.ProductListGetVm(
                java.util.List.of(), 0, 10, 0, 0, true
        );
        assertThat(vm.pageNo()).isEqualTo(0);
        assertThat(vm.pageSize()).isEqualTo(10);
    }
}
