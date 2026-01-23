package com.github.renancvitor.inventory.infra.messaging.kafka.producer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.events.StockBelowMinimumEvent;
import com.github.renancvitor.inventory.infra.messaging.kafka.contract.KafkaTopics;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "kafka")
@RequiredArgsConstructor
public class StockBelowMinimumKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(StockBelowMinimumEvent event) {
        kafkaTemplate.send(KafkaTopics.STOCK_BELOW_MINIMUM_V1, event);
    }

}
