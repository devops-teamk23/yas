package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void hasText_whenInputIsNull_thenReturnsFalse() {
        assertFalse(StringUtils.hasText(null));
    }

    @Test
    void hasText_whenInputIsEmpty_thenReturnsFalse() {
        assertFalse(StringUtils.hasText(""));
    }

    @Test
    void hasText_whenInputIsBlank_thenReturnsFalse() {
        assertFalse(StringUtils.hasText("   "));
    }

    @Test
    void hasText_whenInputHasText_thenReturnsTrue() {
        assertTrue(StringUtils.hasText("text"));
        assertTrue(StringUtils.hasText(" text "));
    }
}
