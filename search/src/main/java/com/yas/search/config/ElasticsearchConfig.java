package com.yas.search.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.support.HttpHeaders;

import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris:elasticsearch-es-http.elasticsearch:9200}")
    private String uris;

    @Value("${spring.elasticsearch.username:elastic}")
    private String username;

    @Value("${spring.elasticsearch.password:}")
    private String password;

    @Override
    public ClientConfiguration clientConfiguration() {
        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.add("Accept", "application/vnd.elasticsearch+json;compatible-with=8");
        defaultHeaders.add("Content-Type", "application/vnd.elasticsearch+json;compatible-with=8");

        return ClientConfiguration.builder()
                .connectedTo(uris.replace("http://", "").replace("https://", ""))
                .withBasicAuth(username, password)
                .withDefaultHeaders(defaultHeaders)
                .build();
    }
}
