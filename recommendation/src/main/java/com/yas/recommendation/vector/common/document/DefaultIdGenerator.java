package com.yas.recommendation.vector.common.document;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import org.springframework.ai.document.id.IdGenerator;

// TODO: currently, it will be used for all document, consider to make this overridable.
public class DefaultIdGenerator implements IdGenerator {

    private final Long identity;
    private final String idPrefix;
    // Namespace UUID for YAS recommendation service
    private static final UUID NAMESPACE = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");

    public DefaultIdGenerator(String idPrefix, Long identity) {
        this.identity = identity;
        this.idPrefix = idPrefix;
    }

    @Override
    public String generateId(Object... contents) {
        var id = "%s-%s".formatted(idPrefix, identity);
        return generateUuidV5(NAMESPACE, id).toString();
    }

    /**
     * Generates a UUID v5 (SHA-1 name-based) UUID.
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
}
