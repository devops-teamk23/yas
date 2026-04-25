package com.yas.product.utils;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UtilsCoverageTest {

    @Test
    void testConstants() {
        Constants constants = new Constants();
        Constants.ErrorCode errorCode = constants.new ErrorCode();
        assertThat(Constants.ErrorCode.PRODUCT_NOT_FOUND).isEqualTo("PRODUCT_NOT_FOUND");
        assertThat(errorCode).isNotNull();
    }

    @Test
    void testMessagesUtils() {
        MessagesUtils messagesUtils = new MessagesUtils();
        assertThat(messagesUtils).isNotNull();
        String msg = MessagesUtils.getMessage("SOME_DUMMY_CODE", "arg1");
        assertThat(msg).isNotNull();
    }

    @Test
    void testProductConverter() {
        ProductConverter converter = new ProductConverter();
        assertThat(converter).isNotNull();
        
        String slug1 = ProductConverter.toSlug(" Hello World! - Test--Slug ");
        assertThat(slug1).isEqualTo("hello-world-test-slug");
        
        String slug2 = ProductConverter.toSlug("-Starts With Dash");
        assertThat(slug2).isEqualTo("starts-with-dash");
    }
}
