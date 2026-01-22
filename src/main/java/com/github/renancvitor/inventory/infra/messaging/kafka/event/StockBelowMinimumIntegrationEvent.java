package com.github.renancvitor.inventory.infra.messaging.kafka.event;

public record StockBelowMinimumIntegrationEvent(
        Long productId,
        Long userId,
        Integer currentStock,
        Integer minimumStock,
        String ocurredAt) {

}
