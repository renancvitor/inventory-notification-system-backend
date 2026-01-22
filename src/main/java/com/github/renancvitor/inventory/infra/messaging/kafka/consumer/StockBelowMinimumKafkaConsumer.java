package com.github.renancvitor.inventory.infra.messaging.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StockBelowMinimumKafkaConsumer {

    @KafkaListener(topics = "stock-below-minimum-topic", groupId = "stock-below-minimum-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consume(Object payload) {
        System.out.println("Received Stock Below Minimum Event");
        System.out.println(payload);
    }

}
