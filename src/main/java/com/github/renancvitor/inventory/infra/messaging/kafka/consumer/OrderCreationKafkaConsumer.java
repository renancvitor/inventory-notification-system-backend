package com.github.renancvitor.inventory.infra.messaging.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreationKafkaConsumer {

    @KafkaListener(topics = "order-creation-topic", groupId = "order-created-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consume(Object payload) {
        System.out.println("Received Order Creation Event");
        System.out.println(payload);

        throw new RuntimeException("Erro proposital");
    }

    @KafkaListener(topics = "order-creation-topic.DLT", groupId = "order-created-dlt-consumer", containerFactory = "dlqKafkaListenerContainerFactory")
    public void dlq(Object payload) {
        System.out.println("Received Order Creation DLT Event");
        System.out.println(payload);
    }

}
