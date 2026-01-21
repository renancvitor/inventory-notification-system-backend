package com.github.renancvitor.inventory.domain.events;

import java.time.Instant;

public record StockBelowMinimumEvent(
                Long productId,
                Long userId,
                Integer currentStock,
                Integer minimumStock,
                Instant ocurredAt) implements BusinessEvent {

}
