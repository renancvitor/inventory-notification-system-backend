package com.github.renancvitor.inventory.application.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;

public record OrderFilter(
        Integer orderStatusId,
        Long requestedBy,
        Long approvedBy,
        Long rejectedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        BigDecimal totalValue) {

    @AssertTrue(message = "valor total não pode ser negativo")
    public boolean isTotalValueValid() {
        return totalValue == null || totalValue.compareTo(BigDecimal.ZERO) >= 0;
    }
}
