package com.yas.recommendation.vector.common.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import tools.jackson.databind.ObjectMapper;

class DocumentRowMapperTest {

    @Test
    void shouldMapResultSetToDocument() throws Exception {
        ObjectMapper objectMapper = org.mockito.Mockito.mock(ObjectMapper.class);
        ResultSet resultSet = org.mockito.Mockito.mock(ResultSet.class);
        when(resultSet.getString("id")).thenReturn("doc-1");
        when(resultSet.getString("content")).thenReturn("Phone content");
        when(resultSet.getObject("metadata")).thenReturn("{\"id\":1}");
        when(objectMapper.readValue("{\"id\":1}", Map.class)).thenReturn(Map.of("id", 1));

        Document document = new DocumentRowMapper(objectMapper).mapRow(resultSet, 0);

        assertThat(document.getId()).isEqualTo("doc-1");
        assertThat(document.getContent()).isEqualTo("Phone content");
        assertThat(document.getMetadata()).containsEntry("id", 1);
    }
}
