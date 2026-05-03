package com.yas.location.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ConstantsTest {

    @Test
    void testErrorCodeConstants_haveExpectedValues() {
        assertEquals("COUNTRY_NOT_FOUND", Constants.ErrorCode.COUNTRY_NOT_FOUND);
        assertEquals("NAME_ALREADY_EXITED", Constants.ErrorCode.NAME_ALREADY_EXITED);
        assertEquals("STATE_OR_PROVINCE_NOT_FOUND", Constants.ErrorCode.STATE_OR_PROVINCE_NOT_FOUND);
        assertEquals("ADDRESS_NOT_FOUND", Constants.ErrorCode.ADDRESS_NOT_FOUND);
        assertEquals("CODE_ALREADY_EXISTED", Constants.ErrorCode.CODE_ALREADY_EXISTED);
    }

    @Test
    void testPageableConstants_haveExpectedDefaults() {
        assertEquals("10", Constants.PageableConstant.DEFAULT_PAGE_SIZE);
        assertEquals("0", Constants.PageableConstant.DEFAULT_PAGE_NUMBER);
    }

    @Test
    void testApiConstants_haveExpectedKeyValues() {
        assertEquals("/storefront/countries", Constants.ApiConstant.COUNTRIES_STOREFRONT_URL);
        assertEquals("/storefront/state-or-provinces", Constants.ApiConstant.STATE_OR_PROVINCES_STOREFRONT_URL);
        assertEquals("204", Constants.ApiConstant.CODE_204);
        assertEquals("No content", Constants.ApiConstant.NO_CONTENT);
    }
}
