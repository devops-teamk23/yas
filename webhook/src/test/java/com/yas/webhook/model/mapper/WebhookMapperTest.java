package com.yas.webhook.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.webhook.model.Event;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.enums.EventName;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;

class WebhookMapperTest {

    private final WebhookMapper mapper = new WebhookMapperImpl();

    @Test
    void shouldMapWebhookToListAndDetailViewModels() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        webhook.setPayloadUrl("https://example.test/hook");
        webhook.setContentType("application/json");
        webhook.setSecret("secret");
        webhook.setIsActive(true);
        WebhookEvent eventLink = new WebhookEvent();
        eventLink.setEventId(3L);
        webhook.setWebhookEvents(List.of(eventLink));

        var vm = mapper.toWebhookVm(webhook);
        var detail = mapper.toWebhookDetailVm(webhook);
        var page = mapper.toWebhookListGetVm(new PageImpl<>(List.of(webhook)), 0, 10);

        assertThat(vm.getId()).isEqualTo(1L);
        assertThat(vm.getPayloadUrl()).isEqualTo("https://example.test/hook");
        assertThat(detail.getEvents()).extracting(EventVm::getId).containsExactly(3L);
        assertThat(detail.getSecret()).isNull();
        assertThat(page.getWebhooks()).hasSize(1);
        assertThat(page.getPageNo()).isZero();
        assertThat(page.getPageSize()).isEqualTo(10);
    }

    @Test
    void shouldMapCreateAndUpdateRequests() {
        WebhookPostVm request = new WebhookPostVm(
                "https://example.test/new",
                "new-secret",
                "application/json",
                true,
                Collections.emptyList()
        );
        Webhook existing = new Webhook();
        existing.setId(5L);

        Webhook created = mapper.toCreatedWebhook(request);
        Webhook updated = mapper.toUpdatedWebhook(existing, request);

        assertThat(created.getId()).isNull();
        assertThat(created.getPayloadUrl()).isEqualTo(request.getPayloadUrl());
        assertThat(created.getSecret()).isEqualTo(request.getSecret());
        assertThat(created.getIsActive()).isTrue();
        assertThat(updated.getId()).isEqualTo(5L);
        assertThat(updated.getPayloadUrl()).isEqualTo(request.getPayloadUrl());
    }

    @Test
    void shouldHandleEmptyWebhookEvents() {
        assertThat(mapper.toWebhookEventVms(null)).isEmpty();
        assertThat(mapper.toWebhookEventVms(Collections.emptyList())).isEmpty();
    }

    @Test
    void shouldMapEventEntity() {
        Event event = new Event();
        event.setId(2L);
        event.setName(EventName.ON_PRODUCT_UPDATED);

        var eventVm = new EventMapperImpl().toEventVm(event);

        assertThat(eventVm.getId()).isEqualTo(2L);
        assertThat(eventVm.getName()).isEqualTo(EventName.ON_PRODUCT_UPDATED);
    }
}
