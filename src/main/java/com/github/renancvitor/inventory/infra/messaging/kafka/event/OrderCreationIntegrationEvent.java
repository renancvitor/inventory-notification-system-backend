package com.github.renancvitor.inventory.infra.messaging.kafka.event;

public record OrderCreationIntegrationEvent(
        Long orderId,
        Long userId,
        String createdAt) {

}
