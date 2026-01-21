package com.github.renancvitor.inventory.infra.messaging.kafka.publisher;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.events.OrderCreationEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCreationKafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @EventListener
    public void publishOrderCreatedEvent(OrderCreationEvent event) {
        kafkaTemplate.send("order-creation-topic", event);
    }

}
