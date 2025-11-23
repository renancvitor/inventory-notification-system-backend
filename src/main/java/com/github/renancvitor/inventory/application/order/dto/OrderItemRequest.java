package com.github.renancvitor.inventory.application.order.dto;

import java.math.BigDecimal;

public record OrderItemRequest(
                Long productId,
                Integer movementTypeId,
                Integer quantity,
                BigDecimal unitPrice) {
}
