package com.github.renancvitor.inventory.infra.messaging.kafka.publisher;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.events.StockBelowMinimumEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockBelowMinimumKafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @EventListener
    public void handle(StockBelowMinimumEvent event) {
        kafkaTemplate.send("stock-below-minimum-topic", event);
    }

}
