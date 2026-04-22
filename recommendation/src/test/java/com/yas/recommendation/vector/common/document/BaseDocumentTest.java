package com.yas.recommendation.vector.common.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yas.recommendation.vector.product.document.ProductDocument;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

class BaseDocumentTest {

    @Test
    void shouldConvertAnnotatedBaseDocumentToSpringDocument() {
        ProductDocument productDocument = new ProductDocument();
        productDocument.setContent("Phone content");
        productDocument.setMetadata(Map.of("id", 1L));

        Document document = productDocument.toDocument(new DefaultIdGenerator("PRODUCT", 1L));

        assertThat(document.getContent()).isEqualTo("Phone content");
        assertThat(document.getMetadata()).containsEntry("id", 1L);
        assertThat(document.getId()).isNotBlank();
    }

    @Test
    void shouldRequireContentAndMetadata() {
        ProductDocument productDocument = new ProductDocument();

        assertThatThrownBy(() -> productDocument.toDocument(new DefaultIdGenerator("PRODUCT", 1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("content cannot be null");
    }
}
