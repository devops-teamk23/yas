package com.yas.webhook.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.service.WebhookService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

class WebhookControllerTest {

    private final WebhookService webhookService = org.mockito.Mockito.mock(WebhookService.class);
    private final WebhookController webhookController = new WebhookController(webhookService);

    @Test
    void shouldReturnPageableWebhooks() {
        WebhookListGetVm expected = WebhookListGetVm.builder().pageNo(1).pageSize(5).build();
        when(webhookService.getPageableWebhooks(1, 5)).thenReturn(expected);

        var response = webhookController.getPageableWebhooks(1, 5);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expected);
    }

    @Test
    void shouldReturnAllWebhooks() {
        WebhookVm webhook = new WebhookVm();
        when(webhookService.findAllWebhooks()).thenReturn(List.of(webhook));

        var response = webhookController.listWebhooks();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(webhook);
    }

    @Test
    void shouldReturnWebhookById() {
        WebhookDetailVm webhook = new WebhookDetailVm();
        when(webhookService.findById(7L)).thenReturn(webhook);

        var response = webhookController.getWebhook(7L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(webhook);
    }

    @Test
    void shouldCreateWebhook() {
        WebhookPostVm request = new WebhookPostVm();
        WebhookDetailVm created = new WebhookDetailVm();
        created.setId(9L);
        when(webhookService.create(request)).thenReturn(created);

        var response = webhookController.createWebhook(request, UriComponentsBuilder.fromUriString("http://localhost"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(created);
        assertThat(response.getHeaders().getLocation()).isNotNull();
    }

    @Test
    void shouldUpdateWebhook() {
        WebhookPostVm request = new WebhookPostVm();

        var response = webhookController.updateWebhook(7L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(webhookService).update(request, 7L);
    }

    @Test
    void shouldDeleteWebhook() {
        var response = webhookController.deleteWebhook(7L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(webhookService).delete(7L);
    }
}
