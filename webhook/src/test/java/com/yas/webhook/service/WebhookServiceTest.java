package com.yas.webhook.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.Event;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.enums.NotificationStatus;
import com.yas.webhook.model.mapper.WebhookMapper;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.repository.EventRepository;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import com.yas.webhook.repository.WebhookEventRepository;
import com.yas.webhook.repository.WebhookRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebhookService Tests")
class WebhookServiceTest {

    @Mock
    private WebhookRepository webhookRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private WebhookEventRepository webhookEventRepository;

    @Mock
    private WebhookEventNotificationRepository webhookEventNotificationRepository;

    @Mock
    private WebhookMapper webhookMapper;

    @Mock
    private WebhookApi webHookApi;

    @InjectMocks
    private WebhookService webhookService;

    private Webhook webhook;
    private WebhookEvent webhookEvent;
    private WebhookVm webhookVm;
    private WebhookDetailVm webhookDetailVm;
    private Event event;

    @BeforeEach
    void setUp() {
        webhook = new Webhook();
        webhook.setId(1L);
        webhook.setPayloadUrl("http://localhost:8080/webhook");
        webhook.setSecret("secret-key");

        webhookEvent = new WebhookEvent();
        webhookEvent.setId(1L);
        webhookEvent.setWebhookId(1L);
        webhookEvent.setEventId(1L);

        event = new Event();
        event.setId(1L);

        webhookVm = new WebhookVm();
        webhookVm.setId(1L);
        webhookVm.setPayloadUrl("http://localhost:8080/webhook");

        webhookDetailVm = new WebhookDetailVm();
        webhookDetailVm.setId(1L);
        webhookDetailVm.setPayloadUrl("http://localhost:8080/webhook");
    }

    @Nested
    @DisplayName("getPageableWebhooks")
    class GetPageableWebhooksTests {
        @Test
        @DisplayName("Should return pageable webhooks")
        void testGetPageableWebhooks_shouldReturnPageableWebhooks() {
            Page<Webhook> page = new PageImpl<>(List.of(webhook));
            WebhookListGetVm expected = new WebhookListGetVm();

            when(webhookRepository.findAll(any(PageRequest.class))).thenReturn(page);
            when(webhookMapper.toWebhookListGetVm(page, 0, 10)).thenReturn(expected);

            WebhookListGetVm result = webhookService.getPageableWebhooks(0, 10);

            assertThat(result).isNotNull();
            verify(webhookRepository).findAll(any(PageRequest.class));
            verify(webhookMapper).toWebhookListGetVm(page, 0, 10);
        }

        @Test
        @DisplayName("Should return empty pageable webhooks")
        void testGetPageableWebhooks_shouldReturnEmptyPageableWebhooks() {
            Page<Webhook> emptyPage = new PageImpl<>(List.of());
            WebhookListGetVm expected = new WebhookListGetVm();

            when(webhookRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);
            when(webhookMapper.toWebhookListGetVm(emptyPage, 0, 10)).thenReturn(expected);

            WebhookListGetVm result = webhookService.getPageableWebhooks(0, 10);

            assertThat(result).isNotNull();
            verify(webhookRepository).findAll(any(PageRequest.class));
        }
    }

    @Nested
    @DisplayName("findAllWebhooks")
    class FindAllWebhooksTests {
        @Test
        @DisplayName("Should return all webhooks")
        void testFindAllWebhooks_shouldReturnAllWebhooks() {
            when(webhookRepository.findAll(any(Sort.class))).thenReturn(List.of(webhook));
            when(webhookMapper.toWebhookVm(webhook)).thenReturn(webhookVm);

            List<WebhookVm> result = webhookService.findAllWebhooks();

            assertThat(result).isNotNull().hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            verify(webhookRepository).findAll(any(Sort.class));
        }

        @Test
        @DisplayName("Should return empty list when no webhooks exist")
        void testFindAllWebhooks_shouldReturnEmptyList() {
            when(webhookRepository.findAll(any(Sort.class))).thenReturn(List.of());

            List<WebhookVm> result = webhookService.findAllWebhooks();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTests {
        @Test
        @DisplayName("Should return webhook by valid ID")
        void testFindById_shouldReturnWebhookById() {
            when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
            when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(webhookDetailVm);

            WebhookDetailVm result = webhookService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(webhookRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw NotFoundException when webhook not found")
        void testFindById_shouldThrowNotFoundExceptionWhenIdNotFound() {
            when(webhookRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> webhookService.findById(999L))
                .isInstanceOf(NotFoundException.class);
            verify(webhookRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("create")
    class CreateTests {
        @Test
        @DisplayName("Should successfully create webhook without events")
        void testCreate_shouldSuccessfullyCreateWebhookWithoutEvents() {
            WebhookPostVm postVm = new WebhookPostVm();
            postVm.setPayloadUrl("http://localhost:8080/webhook");
            postVm.setSecret("secret-key");

            when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(webhook);
            when(webhookRepository.save(webhook)).thenReturn(webhook);
            when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(webhookDetailVm);

            WebhookDetailVm result = webhookService.create(postVm);

            assertThat(result).isNotNull();
            verify(webhookRepository).save(webhook);
            verify(webhookEventRepository, times(0)).saveAll(any());
        }

        @Test
        @DisplayName("Should successfully create webhook with events")
        void testCreate_shouldSuccessfullyCreateWebhookWithEvents() {
            EventVm eventVm = new EventVm();
            eventVm.setId(1L);
            WebhookPostVm postVm = new WebhookPostVm();
            postVm.setPayloadUrl("http://localhost:8080/webhook");
            postVm.setSecret("secret-key");
            postVm.setEvents(List.of(eventVm));

            when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(webhook);
            when(webhookRepository.save(webhook)).thenReturn(webhook);
            when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
            when(webhookEventRepository.saveAll(any())).thenReturn(List.of(webhookEvent));
            when(webhookMapper.toWebhookDetailVm(any(Webhook.class))).thenReturn(webhookDetailVm);

            WebhookDetailVm result = webhookService.create(postVm);

            assertThat(result).isNotNull();
            verify(webhookRepository).save(webhook);
            verify(webhookEventRepository).saveAll(any());
            verify(eventRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw NotFoundException when event not found during create")
        void testCreate_shouldThrowNotFoundExceptionWhenEventNotFound() {
            EventVm eventVm = new EventVm();
            eventVm.setId(999L);
            WebhookPostVm postVm = new WebhookPostVm();
            postVm.setPayloadUrl("http://localhost:8080/webhook");
            postVm.setSecret("secret-key");
            postVm.setEvents(List.of(eventVm));

            when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(webhook);
            when(webhookRepository.save(webhook)).thenReturn(webhook);
            when(eventRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> webhookService.create(postVm))
                .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTests {
        @Test
        @DisplayName("Should successfully update webhook without events")
        void testUpdate_shouldSuccessfullyUpdateWebhookWithoutEvents() {
            WebhookPostVm postVm = new WebhookPostVm();
            postVm.setPayloadUrl("http://localhost:8080/webhook-updated");
            postVm.setSecret("secret-key-updated");

            webhook.setWebhookEvents(List.of());

            when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
            when(webhookMapper.toUpdatedWebhook(webhook, postVm)).thenReturn(webhook);
            when(webhookRepository.save(webhook)).thenReturn(webhook);

            webhookService.update(postVm, 1L);

            verify(webhookRepository).findById(1L);
            verify(webhookRepository).save(webhook);
            verify(webhookEventRepository).deleteAll(any());
        }

        @Test
        @DisplayName("Should successfully update webhook with events")
        void testUpdate_shouldSuccessfullyUpdateWebhookWithEvents() {
            EventVm eventVm = new EventVm();
            eventVm.setId(1L);
            WebhookPostVm postVm = new WebhookPostVm();
            postVm.setPayloadUrl("http://localhost:8080/webhook-updated");
            postVm.setSecret("secret-key-updated");
            postVm.setEvents(List.of(eventVm));

            webhook.setWebhookEvents(List.of(webhookEvent));

            when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
            when(webhookMapper.toUpdatedWebhook(webhook, postVm)).thenReturn(webhook);
            when(webhookRepository.save(webhook)).thenReturn(webhook);
            when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
            when(webhookEventRepository.saveAll(any())).thenReturn(List.of(webhookEvent));

            webhookService.update(postVm, 1L);

            verify(webhookRepository).findById(1L);
            verify(webhookRepository).save(webhook);
            verify(webhookEventRepository).deleteAll(any());
            verify(webhookEventRepository).saveAll(any());
        }

        @Test
        @DisplayName("Should throw NotFoundException when webhook not found during update")
        void testUpdate_shouldThrowNotFoundExceptionWhenWebhookNotFound() {
            WebhookPostVm postVm = new WebhookPostVm();

            when(webhookRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> webhookService.update(postVm, 999L))
                .isInstanceOf(NotFoundException.class);
            verify(webhookRepository).findById(999L);
        }

        @Test
        @DisplayName("Should throw NotFoundException when event not found during update")
        void testUpdate_shouldThrowNotFoundExceptionWhenEventNotFound() {
            EventVm eventVm = new EventVm();
            eventVm.setId(999L);
            WebhookPostVm postVm = new WebhookPostVm();
            postVm.setEvents(List.of(eventVm));

            webhook.setWebhookEvents(List.of());

            when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
            when(webhookMapper.toUpdatedWebhook(webhook, postVm)).thenReturn(webhook);
            when(webhookRepository.save(webhook)).thenReturn(webhook);
            when(eventRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> webhookService.update(postVm, 1L))
                .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTests {
        @Test
        @DisplayName("Should successfully delete webhook")
        void testDelete_shouldSuccessfullyDeleteWebhook() {
            when(webhookRepository.existsById(1L)).thenReturn(true);

            webhookService.delete(1L);

            verify(webhookRepository).existsById(1L);
            verify(webhookEventRepository).deleteByWebhookId(1L);
            verify(webhookRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw NotFoundException when webhook not found during delete")
        void testDelete_shouldThrowNotFoundExceptionWhenWebhookNotFound() {
            when(webhookRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> webhookService.delete(999L))
                .isInstanceOf(NotFoundException.class);
            verify(webhookRepository).existsById(999L);
        }
    }

    @Nested
    @DisplayName("notifyToWebhook")
    class NotifyToWebhookTests {
        @Test
        @DisplayName("Should successfully notify to webhook")
        void testNotifyToWebhook_shouldSuccessfullyNotify() {
            WebhookEventNotification notification = new WebhookEventNotification();
            notification.setId(1L);
            notification.setNotificationStatus(NotificationStatus.NOTIFYING);

            WebhookEventNotificationDto notificationDto = new WebhookEventNotificationDto();
            notificationDto.setNotificationId(1L);
            notificationDto.setUrl("http://localhost:8080/webhook");
            notificationDto.setSecret("secret-key");

            when(webhookEventNotificationRepository.findById(1L)).thenReturn(Optional.of(notification));
            when(webhookEventNotificationRepository.save(notification)).thenReturn(notification);

            webhookService.notifyToWebhook(notificationDto);

            assertThat(notification.getNotificationStatus()).isEqualTo(NotificationStatus.NOTIFIED);
            verify(webHookApi).notify(
                notificationDto.getUrl(),
                notificationDto.getSecret(),
                notificationDto.getPayload()
            );
            verify(webhookEventNotificationRepository).save(notification);
        }

        @Test
        @DisplayName("Should throw exception when notification not found")
        void testNotifyToWebhook_shouldThrowExceptionWhenNotificationNotFound() {
            WebhookEventNotificationDto notificationDto = new WebhookEventNotificationDto();
            notificationDto.setNotificationId(999L);
            notificationDto.setUrl("http://localhost:8080/webhook");
            notificationDto.setSecret("secret-key");

            when(webhookEventNotificationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> webhookService.notifyToWebhook(notificationDto))
                .isInstanceOf(Exception.class);
        }
    }
}
