package com.yas.storefrontbff.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ServiceUrlConfigTest {

    @Test
    void shouldExposeConfiguredServiceUrls() {
        ServiceUrlConfig config = new ServiceUrlConfig(Map.of("cart", "http://cart:8080"));

        assertThat(config.services()).containsEntry("cart", "http://cart:8080");
    }
}
