package com.github.renancvitor.inventory.infra.messaging.kafka.bridge;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.events.OrderCreationEvent;
import com.github.renancvitor.inventory.infra.messaging.kafka.producer.OrderKafkaProducer;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "kafka")
@RequiredArgsConstructor
public class OrderEventKafkaHandler {

    private final OrderKafkaProducer producer;

    @EventListener
    public void handle(OrderCreationEvent event) {
        producer.send(event);
    }

}
