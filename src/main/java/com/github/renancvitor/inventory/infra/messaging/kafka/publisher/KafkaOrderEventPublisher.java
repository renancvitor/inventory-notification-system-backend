package com.github.renancvitor.inventory.infra.messaging.kafka.publisher;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.events.OrderCreationEvent;
import com.github.renancvitor.inventory.domain.events.OrderEventPublisher;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "kafka")
@RequiredArgsConstructor
public class KafkaOrderEventPublisher implements OrderEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishOrderCreatedEvent(OrderCreationEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

}
