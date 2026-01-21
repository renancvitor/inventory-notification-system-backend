package com.github.renancvitor.inventory.domain.events;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderCreationEvent(
        Long orderId,
        Long userId,
        BigDecimal totalValue,
        Instant ocurredAt) implements BusinessEvent {

}
