package com.github.renancvitor.inventory.infra.outbox.publisher;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.renancvitor.inventory.infra.outbox.entity.OutboxEventEntity;
import com.github.renancvitor.inventory.infra.outbox.entity.OutboxStatus;
import com.github.renancvitor.inventory.infra.outbox.repository.OutboxEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void saveEvent(
            String aggregateType,
            Long aggregateId,
            String eventType,
            String eventVersion,
            Object payload) {
        try {
            OutboxEventEntity event = OutboxEventEntity.builder()
                    .id(UUID.randomUUID())
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .eventVersion(eventVersion)
                    .payload(objectMapper.writeValueAsString(payload))
                    .eventStatus(OutboxStatus.PENDING)
                    .createdAt(Instant.now())
                    .build();

            outboxEventRepository.save(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize outbox event", e);
        }
    }

}
