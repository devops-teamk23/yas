package com.yas.recommendation.vector.product.formatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.yas.recommendation.viewmodel.CategoryVm;
import com.yas.recommendation.viewmodel.ProductAttributeValueVm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ProductDocumentFormatterTest {

    @Mock
    private ObjectMapper objectMapper;

    private final ProductDocumentFormatter formatter = new ProductDocumentFormatter();

    @Test
    void shouldFormatProductContentAndRemoveHtmlTags() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "<b>Phone</b>");
        entityMap.put("categories", new ArrayList<>(List.of(Map.of("name", "Mobile"))));
        entityMap.put("attributeValues", new ArrayList<>(List.of(Map.of("value", "128GB"))));

        when(objectMapper.convertValue(any(), eq(CategoryVm.class)))
                .thenReturn(new CategoryVm(1L, "Mobile", null, null, null, null, null, true));
        when(objectMapper.convertValue(any(), eq(ProductAttributeValueVm.class)))
                .thenReturn(new ProductAttributeValueVm(1L, "Storage", "128GB"));

        String result = formatter.format(
                entityMap,
                "{name}| {categories}| {attributeValues}",
                objectMapper
        );

        assertThat(result).isEqualTo("Phone| [Mobile]| [Storage: 128GB]");
    }

    @Test
    void shouldFormatMissingCollectionsAsEmptyLists() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "Phone");

        String result = formatter.format(
                entityMap,
                "{name}| {categories}| {attributeValues}",
                objectMapper
        );

        assertThat(result).isEqualTo("Phone| []| []");
    }
}
