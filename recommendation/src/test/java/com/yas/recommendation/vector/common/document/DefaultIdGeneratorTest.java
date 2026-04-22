package com.yas.recommendation.vector.common.document;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DefaultIdGeneratorTest {

    @Test
    void shouldGenerateStableUuidFromPrefixAndIdentity() {
        DefaultIdGenerator first = new DefaultIdGenerator("PRODUCT", 99L);
        DefaultIdGenerator second = new DefaultIdGenerator("PRODUCT", 99L);
        DefaultIdGenerator different = new DefaultIdGenerator("PRODUCT", 100L);

        assertThat(first.generateId()).isEqualTo(second.generateId());
        assertThat(first.generateId()).isNotEqualTo(different.generateId());
    }
}
