package com.yas.recommendation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.yas.recommendation.vector.common.query.VectorQuery;
import com.yas.recommendation.vector.product.document.ProductDocument;
import com.yas.recommendation.viewmodel.RelatedProductVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EmbeddingQueryControllerTest {

    @Test
    void shouldDelegateSimilaritySearchToQueryService() {
        @SuppressWarnings("unchecked")
        VectorQuery<ProductDocument, RelatedProductVm> relatedProductSearch = Mockito.mock(VectorQuery.class);
        RelatedProductVm relatedProduct = new RelatedProductVm();
        when(relatedProductSearch.similaritySearch(5L)).thenReturn(List.of(relatedProduct));

        EmbeddingQueryController controller = new EmbeddingQueryController(relatedProductSearch);

        assertThat(controller.searchProduct(5L)).containsExactly(relatedProduct);
    }
}
