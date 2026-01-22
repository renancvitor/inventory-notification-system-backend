package com.github.renancvitor.inventory.infra.messaging.kafka.publisher;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.events.StockBelowMinimumEvent;
import com.github.renancvitor.inventory.domain.events.StockBelowMininumPublisher;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(value = "messaging.kafka.enabled", havingValue = "true")
@RequiredArgsConstructor
public class KafkaStockEventPublisher implements StockBelowMininumPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishStockBelowMinimumEvent(StockBelowMinimumEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

}
