package com.github.renancvitor.inventory.infra.messaging.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.renancvitor.inventory.domain.events.DomainEventEnvelope;
import com.github.renancvitor.inventory.domain.events.StockBelowMinimumEvent;
import com.github.renancvitor.inventory.infra.messaging.kafka.idempotency.ProcessedEvent;
import com.github.renancvitor.inventory.infra.messaging.kafka.idempotency.ProcessedEventRepository;
import com.github.renancvitor.inventory.infra.messaging.kafka.topic.KafkaTopics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "kafka")
@RequiredArgsConstructor
public class StockBelowMinimumKafkaConsumer {

    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    @RetryableTopic(attempts = "4", backoff = @Backoff(delay = 5000, multiplier = 2.0), retryTopicSuffix = "-RETRY", dltTopicSuffix = "-DLT")
    @KafkaListener(topics = KafkaTopics.STOCK_BELOW_MINIMUM_V1, groupId = "stock-below-minimum-consumer")
    public void consume(String message) {
        DomainEventEnvelope<StockBelowMinimumEvent> envelope = parseEnvelope(message);

        if (processedEventRepository.existsById(envelope.eventId())) {
            log.warn("Duplicated event ignored: {}", envelope.eventId());
            return;
        }

        try {
            MDC.put("correlationId", envelope.correlationId());

            log.info("Consuming event type={} version={} payload={}",
                    envelope.eventId(),
                    envelope.version(),
                    envelope.payload());

            processedEventRepository.save(new ProcessedEvent(envelope.eventId()));
        } finally {
            MDC.clear();
        }
    }

    @DltHandler
    public void dlt(ConsumerRecord<String, String> record) {
        DomainEventEnvelope<?> envelope = parseEnvelope(record.value());

        MDC.put("correlationId", envelope.correlationId());
        log.error(
                "DLT reached | topic={} | partition={} | offset={} | eventType={} | payload={}",
                record.topic(),
                record.partition(),
                record.offset(),
                envelope.eventType(),
                envelope.payload());
    }

    private DomainEventEnvelope<StockBelowMinimumEvent> parseEnvelope(String message) {
        try {
            return objectMapper.readValue(
                    message,
                    new TypeReference<DomainEventEnvelope<StockBelowMinimumEvent>>() {
                    });
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to parse stock below minimum event payload", exception);
        }
    }

}
