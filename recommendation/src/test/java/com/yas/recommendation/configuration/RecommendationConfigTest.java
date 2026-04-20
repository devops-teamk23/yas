package com.yas.recommendation.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Unit tests for RecommendationConfig configuration class.
 * Tests verify correct loading of configuration properties from application.yml/properties.
 */
@SpringBootTest
@EnableConfigurationProperties(RecommendationConfig.class)
@TestPropertySource(properties = "yas.services.product=http://product-service:8080/api")
@DisplayName("RecommendationConfig Tests")
class RecommendationConfigTest {

    @Autowired
    private RecommendationConfig config;

    @Nested
    @DisplayName("Configuration Loading")
    class ConfigurationLoading {

        @Test
        @DisplayName("Should load RecommendationConfig bean successfully")
        void testConfigBeanLoaded() {
            assertNotNull(config, "RecommendationConfig bean should be loaded");
        }

        @Test
        @DisplayName("Should load apiUrl property from configuration")
        void testApiUrlPropertyLoaded() {
            String apiUrl = config.getApiUrl();
            assertNotNull(apiUrl, "API URL should not be null");
            assertEquals("http://product-service:8080/api", apiUrl, 
                    "API URL should match configured value");
        }
    }

    @Nested
    @DisplayName("ApiUrl Configuration")
    class ApiUrlConfiguration {

        @Test
        @DisplayName("Should return non-empty API URL")
        void testApiUrlNotEmpty() {
            String apiUrl = config.getApiUrl();
            assertNotNull(apiUrl, "API URL should not be null");
            assertEquals(false, apiUrl.isEmpty(), "API URL should not be empty");
        }

        @Test
        @DisplayName("Should return API URL with correct scheme")
        void testApiUrlHasHttpScheme() {
            String apiUrl = config.getApiUrl();
            assertEquals(true, apiUrl.startsWith("http://"), 
                    "API URL should start with http:// scheme");
        }

        @Test
        @DisplayName("Should return API URL with correct host")
        void testApiUrlHostIsConfigured() {
            String apiUrl = config.getApiUrl();
            assertEquals("http://product-service:8080/api", apiUrl, 
                    "API URL should contain configured host and port");
        }

        @Test
        @DisplayName("Should consistently return same API URL on multiple calls")
        void testApiUrlConsistency() {
            String url1 = config.getApiUrl();
            String url2 = config.getApiUrl();
            assertEquals(url1, url2, "API URL should be consistent across calls");
        }
    }

    @Nested
    @DisplayName("Configuration with Different Values")
    class ConfigurationVariants {

        @Test
        @DisplayName("Should load configuration with product service endpoint")
        void testProductServiceEndpoint() {
            String apiUrl = config.getApiUrl();
            assertEquals("http://product-service:8080/api", apiUrl,
                    "Should load product service endpoint");
        }
    }
}
