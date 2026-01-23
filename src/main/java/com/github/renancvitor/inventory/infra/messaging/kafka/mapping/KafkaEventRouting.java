package com.github.renancvitor.inventory.infra.messaging.kafka.mapping;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.events.EventTypes;
import com.github.renancvitor.inventory.infra.messaging.kafka.contract.KafkaTopics;

@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "kafka")
public class KafkaEventRouting {

    public String resolveTopic(String eventType, String eventVersion) {
        if (EventTypes.ORDER_CREATED.equals(eventType) && "v1".equals(eventVersion)) {
            return KafkaTopics.ORDER_CREATED_V1;
        }

        if (EventTypes.STOCK_BELOW_MINIMUM.equals(eventType) && "v1".equals(eventVersion)) {
            return KafkaTopics.STOCK_BELOW_MINIMUM_V1;
        }

        throw new IllegalArgumentException(
                String.format("No Kafka topic mapping for eventType=" + eventType + ", version=" + eventVersion));
    }

}
