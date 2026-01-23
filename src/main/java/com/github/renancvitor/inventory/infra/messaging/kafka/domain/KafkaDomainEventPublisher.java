package com.github.renancvitor.inventory.infra.messaging.kafka.domain;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.events.BusinessEvent;
import com.github.renancvitor.inventory.domain.events.DomainEventPublisher;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "kafka")
@RequiredArgsConstructor
public class KafkaDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(BusinessEvent event) {
        publisher.publishEvent(event);
    }
}
