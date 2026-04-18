package com.yas.webhook.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.enums.NotificationStatus;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AbstractWebhookEventNotificationService Tests")
class AbstractWebhookEventNotificationServiceTest {

    @Mock
    private WebhookEventNotificationRepository webhookEventNotificationRepository;

    private ConcreteWebhookEventNotificationService concreteService;
    private WebhookEvent webhookEvent;
    private Webhook webhook;

    @BeforeEach
    void setUp() {
        concreteService = new ConcreteWebhookEventNotificationService(webhookEventNotificationRepository);

        webhook = new Webhook();
        webhook.setId(1L);
        webhook.setPayloadUrl("http://localhost:8080/webhook");
        webhook.setSecret("secret-key");

        webhookEvent = new WebhookEvent();
        webhookEvent.setId(1L);
        webhookEvent.setWebhookId(1L);
        webhookEvent.setEventId(1L);
        webhookEvent.setWebhook(webhook);
    }

    @Nested
    @DisplayName("persistNotification")
    class PersistNotificationTests {
        @Test
        @DisplayName("Should successfully persist notification")
        void testPersistNotification_shouldSuccessfullyPersistNotification() {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("orderId", 123);
            payload.put("status", "PAID");

            WebhookEventNotification savedNotification = new WebhookEventNotification();
            savedNotification.setId(1L);
            savedNotification.setWebhookEventId(1L);
            savedNotification.setPayload(payload.toString());
            savedNotification.setNotificationStatus(NotificationStatus.NOTIFYING);

            when(webhookEventNotificationRepository.save(any(WebhookEventNotification.class)))
                .thenReturn(savedNotification);

            Long notificationId = concreteService.persistNotification(1L, payload);

            assertThat(notificationId).isEqualTo(1L);
            
            ArgumentCaptor<WebhookEventNotification> captor = ArgumentCaptor.forClass(WebhookEventNotification.class);
            verify(webhookEventNotificationRepository).save(captor.capture());
            
            WebhookEventNotification captured = captor.getValue();
            assertThat(captured.getWebhookEventId()).isEqualTo(1L);
            assertThat(captured.getNotificationStatus()).isEqualTo(NotificationStatus.NOTIFYING);
            assertThat(captured.getPayload()).contains("orderId", "123");
        }

        @Test
        @DisplayName("Should persist notification with complex payload")
        void testPersistNotification_shouldPersistWithComplexPayload() {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("customerId", 456);
            payload.put("email", "customer@example.com");

            WebhookEventNotification savedNotification = new WebhookEventNotification();
            savedNotification.setId(2L);

            when(webhookEventNotificationRepository.save(any(WebhookEventNotification.class)))
                .thenReturn(savedNotification);

            Long notificationId = concreteService.persistNotification(2L, payload);

            assertThat(notificationId).isEqualTo(2L);
            verify(webhookEventNotificationRepository).save(any(WebhookEventNotification.class));
        }
    }

    @Nested
    @DisplayName("createNotificationDto")
    class CreateNotificationDtoTests {
        @Test
        @DisplayName("Should successfully create notification dto")
        void testCreateNotificationDto_shouldSuccessfullyCreateDto() {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("orderId", 789);

            WebhookEventNotificationDto dto = concreteService.createNotificationDto(webhookEvent, payload, 1L);

            assertThat(dto).isNotNull();
            assertThat(dto.getUrl()).isEqualTo("http://localhost:8080/webhook");
            assertThat(dto.getSecret()).isEqualTo("secret-key");
            assertThat(dto.getNotificationId()).isEqualTo(1L);
            assertThat(dto.getPayload()).isEqualTo(payload);
        }

        @Test
        @DisplayName("Should create notification dto with different webhook data")
        void testCreateNotificationDto_shouldCreateDtoWithDifferentWebhookData() {
            // Setup different webhook
            Webhook webhookB = new Webhook();
            webhookB.setPayloadUrl("http://webhook-b.com/events");
            webhookB.setSecret("secret-b");

            WebhookEvent webhookEventB = new WebhookEvent();
            webhookEventB.setId(2L);
            webhookEventB.setWebhook(webhookB);

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();

            WebhookEventNotificationDto dto = concreteService.createNotificationDto(webhookEventB, payload, 2L);

            assertThat(dto.getUrl()).isEqualTo("http://webhook-b.com/events");
            assertThat(dto.getSecret()).isEqualTo("secret-b");
            assertThat(dto.getNotificationId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        @Test
        @DisplayName("Should persist and create dto in sequence")
        void testIntegration_shouldPersistAndCreateDtoInSequence() {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("productId", 100);
            payload.put("quantity", 5);

            WebhookEventNotification savedNotification = new WebhookEventNotification();
            savedNotification.setId(10L);

            when(webhookEventNotificationRepository.save(any(WebhookEventNotification.class)))
                .thenReturn(savedNotification);

            // Step 1: Persist notification
            Long notificationId = concreteService.persistNotification(1L, payload);

            // Step 2: Create dto
            WebhookEventNotificationDto dto = concreteService.createNotificationDto(webhookEvent, payload, notificationId);

            // Verify
            assertThat(notificationId).isEqualTo(10L);
            assertThat(dto.getNotificationId()).isEqualTo(10L);
            assertThat(dto.getUrl()).isEqualTo(webhook.getPayloadUrl());
            assertThat(dto.getSecret()).isEqualTo(webhook.getSecret());
        }
    }

    /**
     * Concrete implementation of AbstractWebhookEventNotificationService for testing
     */
    private static class ConcreteWebhookEventNotificationService extends AbstractWebhookEventNotificationService {

        private final WebhookEventNotificationRepository repository;

        ConcreteWebhookEventNotificationService(WebhookEventNotificationRepository repository) {
            this.repository = repository;
        }

        @Override
        protected WebhookEventNotificationRepository getWebhookEventNotificationRepository() {
            return repository;
        }

        // Expose protected methods for testing
        public Long persistNotification(Long webhookEventId, Object payload) {
            return super.persistNotification(webhookEventId, (tools.jackson.databind.JsonNode) payload);
        }

        public WebhookEventNotificationDto createNotificationDto(WebhookEvent webhookEvent, Object payload, Long notificationId) {
            return super.createNotificationDto(webhookEvent, (tools.jackson.databind.JsonNode) payload, notificationId);
        }
    }
}
