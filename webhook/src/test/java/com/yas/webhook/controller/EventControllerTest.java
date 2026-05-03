package com.yas.webhook.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.service.EventService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class EventControllerTest {

    @Test
    void shouldReturnEvents() {
        EventService eventService = org.mockito.Mockito.mock(EventService.class);
        EventVm event = EventVm.builder().id(1L).build();
        when(eventService.findAllEvents()).thenReturn(List.of(event));

        var response = new EventController(eventService).listWebhooks();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(event);
    }
}
