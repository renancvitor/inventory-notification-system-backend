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
public class OrderCreationKafkaConsumer {

    @RetryableTopic(attempts = "4", backoff = @Backoff(delay = 5000, multiplier = 2.0), retryTopicSuffix = "-RETRY", dltTopicSuffix = "-DLT")
    @KafkaListener(topics = KafkaTopics.ORDER_CREATED_V1, groupId = "order-created-consumer")
    public void consume(Object payload) {
        log.info("Received Order Creation Event: {}", payload);
    }

    @DltHandler
    public void dlq(ConsumerRecord<String, Object> record) {
        log.error(
                "DLT | topic={} | key={} | partition={} | offset={} | payload={}",
                record.topic(),
                record.key(),
                record.partition(),
                record.offset(),
                record.value());
    }

}
