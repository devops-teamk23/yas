package com.yas.webhook.integration.api;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

class WebhookApiTest {

    private final RestClient restClient = org.mockito.Mockito.mock(RestClient.class);
    private final RestClient.RequestBodyUriSpec uriSpec = org.mockito.Mockito.mock(RestClient.RequestBodyUriSpec.class);
    private final RestClient.RequestBodySpec bodySpec = org.mockito.Mockito.mock(RestClient.RequestBodySpec.class);
    private final RestClient.ResponseSpec responseSpec = org.mockito.Mockito.mock(RestClient.ResponseSpec.class);
    private final WebhookApi webhookApi = new WebhookApi(restClient);
    private final ObjectNode payload = new ObjectMapper().createObjectNode().put("id", 1);

    @BeforeEach
    void setUp() {
        when(restClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri("https://example.test/hook")).thenReturn(bodySpec);
        when(bodySpec.body(payload)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(ResponseEntity.ok().build());
    }

    @Test
    void shouldSendNotificationWithSignatureWhenSecretExists() {
        when(bodySpec.header(eq(WebhookApi.X_HUB_SIGNATURE_256), anyString())).thenReturn(bodySpec);

        webhookApi.notify("https://example.test/hook", "secret", payload);

        verify(bodySpec).header(eq(WebhookApi.X_HUB_SIGNATURE_256), anyString());
        verify(bodySpec).body(payload);
        verify(responseSpec).toBodilessEntity();
    }

    @Test
    void shouldSendNotificationWithoutSignatureWhenSecretIsBlank() {
        webhookApi.notify("https://example.test/hook", "", payload);

        verify(bodySpec, never()).header(eq(WebhookApi.X_HUB_SIGNATURE_256), anyString());
        verify(bodySpec).body(payload);
        verify(responseSpec).toBodilessEntity();
    }
}
