package com.github.renancvitor.inventory.infra.messaging.kafka.producer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.events.OrderCreationEvent;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "kafka")
@RequiredArgsConstructor
public class OrderKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(OrderCreationEvent event) {
        kafkaTemplate.send("order.created.v1", event);
    }

}
