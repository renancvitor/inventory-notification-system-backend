package com.github.renancvitor.inventory.infra.messaging.kafka.producer;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.events.BusinessEvent;
import com.github.renancvitor.inventory.domain.events.DomainEventEnvelope;
import com.github.renancvitor.inventory.domain.events.DomainEventPublisher;
import com.github.renancvitor.inventory.domain.events.EventTypes;
import com.github.renancvitor.inventory.infra.messaging.kafka.routing.KafkaEventRouting;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "kafka")
@RequiredArgsConstructor
public class KafkaDomainEventPublisher implements DomainEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaEventRouting routing;

    @Override
    public void publish(BusinessEvent event) {

        String eventType = EventTypes.from(event);
        String version = "v1";

        String correlationId = Optional.ofNullable(MDC.get("correlationId"))
                .orElse(UUID.randomUUID().toString());

        DomainEventEnvelope<BusinessEvent> envelope = new DomainEventEnvelope<>(
                UUID.randomUUID().toString(),
                eventType,
                version,
                event.ocurredAt(),
                "inventory-notification-system",
                correlationId,
                event);

        String topic = routing.resolveTopic(eventType, version);

        kafkaTemplate.send(topic, envelope);
    }
}
