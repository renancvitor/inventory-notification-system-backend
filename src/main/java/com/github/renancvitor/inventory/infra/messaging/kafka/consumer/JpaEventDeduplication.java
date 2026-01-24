package com.github.renancvitor.inventory.infra.messaging.kafka.consumer;

import java.time.Instant;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JpaEventDeduplication implements EventDeduplication {

    private final ProcessedEventRepository processedEventRepository;

    @Override
    public boolean isProcessed(String eventId) {
        return processedEventRepository.existsById(eventId);
    }

    @Override
    public void markAsProcessed(String eventId) {
        processedEventRepository.save(new ProcessedEvent(
                eventId,
                Instant.now()));
    }

}
