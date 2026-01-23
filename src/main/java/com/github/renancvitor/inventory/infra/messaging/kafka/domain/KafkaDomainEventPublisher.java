package com.github.renancvitor.inventory.infra.messaging.kafka.domain;

import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.events.BusinessEvent;
import com.github.renancvitor.inventory.domain.events.DomainEventEnvelope;
import com.github.renancvitor.inventory.domain.events.DomainEventPublisher;
import com.github.renancvitor.inventory.infra.messaging.kafka.contract.KafkaTopics;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "kafka")
@RequiredArgsConstructor
public class KafkaDomainEventPublisher implements DomainEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(BusinessEvent event) {

        DomainEventEnvelope<BusinessEvent> envelope = new DomainEventEnvelope<>(
                UUID.randomUUID().toString(),
                event.getClass().getSimpleName(),
                "v1",
                event.ocurredAt(),
                "inventory-notification-system",
                event);

        String topic = KafkaTopics.resolve(event);

        kafkaTemplate.send(topic, envelope);
    }
}
