package com.yas.webhook.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WebhookUtilsTest {

    @Test
    void shouldCreateStableHmacHash() throws Exception {
        String first = HmacUtils.hash("payload", "secret");
        String second = HmacUtils.hash("payload", "secret");
        String different = HmacUtils.hash("payload-2", "secret");

        assertThat(first).isEqualTo(second);
        assertThat(first).isNotEqualTo(different);
    }

    @Test
    void shouldFallbackToCodeWhenMessageIsMissing() {
        assertThat(MessagesUtils.getMessage("missing.message.code", "value"))
                .isEqualTo("missing.message.code");
    }
}
