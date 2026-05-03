package com.yas.search.kafka.config.consumer;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.kafka.listener.ContainerProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Product CDC Kafka Listener Config Tests")
class ProductCdcKafkaListenerConfigTest {

    @Test
    @DisplayName("Should configure product listener container factory with manual ack mode")
    void shouldConfigureListenerContainerFactoryWithManualAckMode() {
        KafkaProperties kafkaProperties = new KafkaProperties();
        kafkaProperties.setBootstrapServers(List.of("localhost:9092"));

        ProductCdcKafkaListenerConfig config = new ProductCdcKafkaListenerConfig(kafkaProperties);

        var factory = config.listenerContainerFactory();

        assertNotNull(factory, "Listener container factory should be created");
        assertEquals(
                ContainerProperties.AckMode.MANUAL,
                factory.getContainerProperties().getAckMode(),
                "Listener container factory should use manual acknowledgment mode"
        );
    }
}
