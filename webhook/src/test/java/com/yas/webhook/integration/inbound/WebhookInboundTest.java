package com.yas.webhook.integration.inbound;

import static org.mockito.Mockito.verify;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import com.yas.webhook.service.OrderEventService;
import com.yas.webhook.service.ProductEventService;
import org.junit.jupiter.api.Test;

class WebhookInboundTest {

    private final ObjectNode payload = new ObjectMapper().createObjectNode().put("id", 1);

    @Test
    void shouldDelegateProductEvents() {
        ProductEventService productEventService = org.mockito.Mockito.mock(ProductEventService.class);

        new ProductEventInbound(productEventService).onProductEvent(payload);

        verify(productEventService).onProductEvent(payload);
    }

    @Test
    void shouldDelegateOrderEvents() {
        OrderEventService orderEventService = org.mockito.Mockito.mock(OrderEventService.class);

        new OrderEventInbound(orderEventService).onOrderEvent(payload);

        verify(orderEventService).onOrderEvent(payload);
    }
}
