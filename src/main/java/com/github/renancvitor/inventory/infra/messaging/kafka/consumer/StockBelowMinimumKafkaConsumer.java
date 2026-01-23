package com.github.renancvitor.inventory.infra.messaging.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.infra.messaging.kafka.contract.KafkaTopics;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "kafka")
public class StockBelowMinimumKafkaConsumer {

    @RetryableTopic(attempts = "4", backoff = @Backoff(delay = 5000, multiplier = 2.0), retryTopicSuffix = "-RETRY", dltTopicSuffix = "-DLT")
    @KafkaListener(topics = KafkaTopics.STOCK_BELOW_MINIMUM_V1, groupId = "stock-below-minimum-consumer")
    public void consume(Object payload) {
        log.info("Received Stock Below Minimum Event: {}", payload);
    }

    @DltHandler
    public void dlt(ConsumerRecord<String, Object> record) {
        log.error(
                "DLT | topic={} | key={} | partition={} | offset={} | payload={}",
                record.topic(),
                record.key(),
                record.partition(),
                record.offset(),
                record.value());
    }
}