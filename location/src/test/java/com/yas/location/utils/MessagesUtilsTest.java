package com.yas.location.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void testGetMessage_whenCodeExists_formatsMessage() {
        String message = MessagesUtils.getMessage(Constants.ErrorCode.COUNTRY_NOT_FOUND, 10);

        assertEquals("The country 10 is not found", message);
    }

    @Test
    void testGetMessage_whenCodeMissing_returnsOriginalCode() {
        String message = MessagesUtils.getMessage("UNKNOWN_CODE");

        assertEquals("UNKNOWN_CODE", message);
    }
}
