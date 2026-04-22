package com.yas.recommendation.vector.product.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.yas.recommendation.service.ProductService;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;

class ProductVectorRepositoryTest {

    @Test
    void shouldFetchProductDetailFromProductService() {
        VectorStore vectorStore = org.mockito.Mockito.mock(VectorStore.class);
        ProductService productService = org.mockito.Mockito.mock(ProductService.class);
        ProductDetailVm product = new ProductDetailVm(
                10L,
                "Phone",
                "Short",
                "Description",
                "Spec",
                "SKU",
                "GTIN",
                "phone",
                true,
                true,
                false,
                true,
                true,
                10.0,
                1L,
                Collections.emptyList(),
                "Title",
                "Keyword",
                "Meta",
                1L,
                "Brand",
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                Collections.emptyList()
        );
        when(productService.getProductDetail(10L)).thenReturn(product);

        ProductVectorRepository repository = new ProductVectorRepository(vectorStore, productService);

        assertThat(repository.getEntity(10L)).isSameAs(product);
    }
}
