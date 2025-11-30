package com.github.renancvitor.inventory.application.order.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(
        @NotNull Long productId,
        @NotNull Integer movementTypeId,
        @NotNull @Positive Integer quantity,
        @NotNull @Positive BigDecimal unitPrice) {
}
