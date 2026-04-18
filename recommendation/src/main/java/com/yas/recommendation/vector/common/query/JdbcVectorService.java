package com.yas.recommendation.vector.common.query;

import tools.jackson.databind.ObjectMapper;
import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.vector.common.document.BaseDocument;
import com.yas.recommendation.vector.common.document.DocumentMetadata;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.stereotype.Service;

/**
 * Jdbc Vector service support native query vector search for document.
 */
@Service
class JdbcVectorService {

    public static final String DEFAULT_DOCID_PREFIX = "PRODUCT";
    // Namespace UUID for YAS recommendation service
    private static final UUID NAMESPACE = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");

    @Value("${spring.ai.vectorstore.pgvector.table-name:vector_store}")
    private String vectorTableName;

    /* Using JdbcTemplate to keep consistency with Spring AI implementation, instead of using JPA, JdbcClient */
    private final JdbcTemplate jdbcClient;
    private final DocumentRowMapper documentRowMapper;
    private final EmbeddingSearchConfiguration embeddingSearchConfiguration;

    public JdbcVectorService(
            JdbcTemplate jdbcClient,
            ObjectMapper objectMapper,
            EmbeddingSearchConfiguration embeddingSearchConfiguration
    ) {
        this.jdbcClient = jdbcClient;
        this.documentRowMapper = new DocumentRowMapper(objectMapper);
        this.embeddingSearchConfiguration = embeddingSearchConfiguration;
    }

    public <D extends BaseDocument> List<Document> similarityProduct(Long id, Class<D> docType) {
        String docIdPrefix = getDocIdPrefix(docType);
        UUID idStr = generateUuid(docIdPrefix, id);

        return jdbcClient.query(getFormattedQuery(), getPreparedStatementSetter(idStr), documentRowMapper);
    }

    private String getDocIdPrefix(Class<?> docType) {
        return Optional.ofNullable(docType)
                .map(dt -> dt.getAnnotation(DocumentMetadata.class))
                .map(DocumentMetadata::docIdPrefix)
                .orElse(DEFAULT_DOCID_PREFIX);
    }

    private UUID generateUuid(String docIdPrefix, Long id) {
        var nameInput = "%s-%s".formatted(docIdPrefix, id);
        return generateUuidV5(NAMESPACE, nameInput);
    }

    /**
     * Generates a UUID v5 (SHA-1 name-based) UUID.
     * Must match DefaultIdGenerator for consistency.
     *
     * @param namespace the namespace UUID
     * @param name the name to hash
     * @return a UUID v5
     */
    private UUID generateUuidV5(UUID namespace, String name) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] namespaceBytes = uuidToBytes(namespace);
            byte[] nameBytes = name.getBytes();
            byte[] combined = new byte[namespaceBytes.length + nameBytes.length];
            System.arraycopy(namespaceBytes, 0, combined, 0, namespaceBytes.length);
            System.arraycopy(nameBytes, 0, combined, namespaceBytes.length, nameBytes.length);
            byte[] hash = md.digest(combined);

            // Set version to 5 (SHA-1)
            hash[6] = (byte) ((hash[6] & 0x0f) | 0x50);
            // Set variant to RFC 4122
            hash[8] = (byte) ((hash[8] & 0x3f) | 0x80);

            return bytesToUuid(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not available", e);
        }
    }

    /**
     * Converts a UUID to its byte array representation.
     */
    private byte[] uuidToBytes(UUID uuid) {
        byte[] bytes = new byte[16];
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            bytes[i] = (byte) (lsb >>> 8 * (15 - i));
        }
        return bytes;
    }

    /**
     * Converts a byte array (first 16 bytes) to a UUID.
     */
    private UUID bytesToUuid(byte[] bytes) {
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (bytes[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (bytes[i] & 0xff);
        }
        return new UUID(msb, lsb);
    }

    private PreparedStatementSetter getPreparedStatementSetter(UUID idStr) {
        return ps -> {
            StatementCreatorUtils.setParameterValue(ps, 1, Integer.MIN_VALUE, idStr);
            StatementCreatorUtils.setParameterValue(ps, 2, Integer.MIN_VALUE, idStr);
            StatementCreatorUtils.setParameterValue(ps, 3, Integer.MIN_VALUE,
                    embeddingSearchConfiguration.similarityThreshold());
            StatementCreatorUtils.setParameterValue(ps, 4, Integer.MIN_VALUE,
                    embeddingSearchConfiguration.topK());
        };
    }

    private String getFormattedQuery() {
        return """
                WITH entity AS (
                    SELECT
                        id,
                        content,
                        metadata,
                        embedding
                    FROM
                        %s
                    WHERE
                        id = ?
                )
                SELECT
                    vs.id,
                    vs.content,
                    vs.metadata,
                    (vs.embedding <=> entity.embedding) AS similarity
                FROM
                    vector_store vs
                JOIN
                    entity ON true
                WHERE vs.id <> ? AND (vs.embedding <=> entity.embedding) > ?
                ORDER BY
                    similarity
                LIMIT ?
                """.formatted(vectorTableName);
    }
}