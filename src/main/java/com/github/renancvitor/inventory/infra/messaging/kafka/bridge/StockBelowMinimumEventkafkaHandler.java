package com.github.renancvitor.inventory.infra.messaging.kafka.bridge;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.events.StockBelowMinimumEvent;
import com.github.renancvitor.inventory.infra.messaging.kafka.producer.StockBelowMinimumKafkaProducer;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "kafka")
@RequiredArgsConstructor
public class StockBelowMinimumEventkafkaHandler {

    private final StockBelowMinimumKafkaProducer producer;

    @EventListener
    public void handle(StockBelowMinimumEvent event) {
        producer.send(event);
    }

}
