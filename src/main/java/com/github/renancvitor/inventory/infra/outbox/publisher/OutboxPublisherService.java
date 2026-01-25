package com.github.renancvitor.inventory.infra.outbox.publisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.renancvitor.inventory.domain.events.DomainEventEnvelope;
import com.github.renancvitor.inventory.infra.messaging.kafka.routing.KafkaEventRouting;
import com.github.renancvitor.inventory.infra.outbox.entity.OutboxEventEntity;
import com.github.renancvitor.inventory.infra.outbox.entity.OutboxStatus;
import com.github.renancvitor.inventory.infra.outbox.repository.OutboxEventRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "messaging.provider", havingValue = "kafka")
public class OutboxPublisherService {

        private final OutboxEventRepository outboxEventRepository;
        private final KafkaTemplate<String, Object> kafkaTemplate;
        private final KafkaEventRouting kafkaEventRouting;
        private final ObjectMapper objectMapper;

        @Transactional
        public void publishPendingEvent() {
                List<OutboxEventEntity> events = outboxEventRepository.findTop50ByEventStatusOrderByCreatedAtAsc(
                                OutboxStatus.PENDING);

                String correlationId = Optional.ofNullable(MDC.get("correlationId"))
                                .orElse(UUID.randomUUID().toString());

                for (OutboxEventEntity event : events) {
                        try {
                                Object payload = objectMapper.readValue(
                                                event.getPayload(),
                                                Object.class);

                                DomainEventEnvelope<Object> envelope = new DomainEventEnvelope<>(
                                                event.getId().toString(),
                                                event.getEventType(),
                                                event.getEventVersion(),
                                                event.getCreatedAt(),
                                                "inventory-notification-system",
                                                correlationId,
                                                payload);

                                String topic = kafkaEventRouting.resolveTopic(
                                                event.getEventType(),
                                                event.getEventVersion());

                                kafkaTemplate.send(topic, envelope);

                                event.setEventStatus(OutboxStatus.SENT);
                                outboxEventRepository.save(event);

                                log.info("Outbox event sent: {}", event.getId());
                        } catch (Exception e) {
                                log.error("Failed to publish outbox event {}",
                                                event.getId(),
                                                e);
                        }
                }
        }

}
