package com.yas.search.config;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestClientBuilderCustomizer customizer() {
        return builder -> {
            Header[] defaultHeaders = new Header[]{
                    new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
                    new BasicHeader(HttpHeaders.ACCEPT, "application/json")
            };
            builder.setDefaultHeaders(defaultHeaders);
        };
    }
}
