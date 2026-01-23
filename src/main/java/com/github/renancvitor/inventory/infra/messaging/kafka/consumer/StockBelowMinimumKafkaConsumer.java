package com.github.renancvitor.inventory.infra.messaging.kafka.consumer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.infra.messaging.kafka.contract.KafkaTopics;

@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "kafka")
public class StockBelowMinimumKafkaConsumer {

    @KafkaListener(topics = KafkaTopics.STOCK_BELOW_MINIMUM_V1, groupId = "stock-below-minimum-consumer")
    public void consume(Object payload) {
        System.out.println("Received Stock Below Minimum Event");
        System.out.println(payload);
    }

    @KafkaListener(topics = KafkaTopics.STOCK_BELOW_MINIMUM_DLT, groupId = "stock-below-minimum-dlt-consumer")
    public void dlq(Object payload) {
        System.out.println("Received Stock Below Minimum DLT Event");
        System.out.println(payload);
    }

}
