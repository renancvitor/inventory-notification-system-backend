package com.github.renancvitor.inventory.infra.messaging.kafka.consumer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "kafka")
public class StockBelowMinimumKafkaConsumer {

    @KafkaListener(topics = "stock-below-minimum-topic", groupId = "stock-below-minimum-consumer")
    public void consume(Object payload) {
        System.out.println("Received Stock Below Minimum Event");
        System.out.println(payload);
    }

    @KafkaListener(topics = "stock-below-minimum-topic.DLT", groupId = "stock-below-minimum-dlt-consumer")
    public void dlq(Object payload) {
        System.out.println("Received Stock Below Minimum DLT Event");
        System.out.println(payload);
    }

}
