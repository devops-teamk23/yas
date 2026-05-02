package com.yas.tax.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void shouldReturnErrorCodeWhenMessageKeyIsMissing() {
        String result = MessagesUtils.getMessage("missing.tax.message.key");

        assertThat(result).isEqualTo("missing.tax.message.key");
    }
}
