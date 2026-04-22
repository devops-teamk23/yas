package com.yas.recommendation.vector.common.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.vector.product.document.ProductDocument;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

class JdbcVectorServiceTest {

    @Test
    void shouldQuerySimilarProductDocuments() {
        JdbcTemplate jdbcTemplate = org.mockito.Mockito.mock(JdbcTemplate.class);
        ObjectMapper objectMapper = org.mockito.Mockito.mock(ObjectMapper.class);
        EmbeddingSearchConfiguration searchConfiguration = new EmbeddingSearchConfiguration(0.6, 5);
        Document document = new Document("id-1", "content", Map.of("id", 2L));

        when(jdbcTemplate.query(
                anyString(),
                any(PreparedStatementSetter.class),
                org.mockito.ArgumentMatchers.<RowMapper<Document>>any()
        )).thenReturn(List.of(document));

        JdbcVectorService service = new JdbcVectorService(jdbcTemplate, objectMapper, searchConfiguration);
        ReflectionTestUtils.setField(service, "vectorTableName", "vector_store");

        List<Document> results = service.similarityProduct(1L, ProductDocument.class);

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        org.mockito.Mockito.verify(jdbcTemplate).query(
                queryCaptor.capture(),
                any(PreparedStatementSetter.class),
                org.mockito.ArgumentMatchers.<RowMapper<Document>>any()
        );
        assertThat(results).containsExactly(document);
        assertThat(queryCaptor.getValue()).contains("FROM", "vector_store", "LIMIT ?");
    }
}
