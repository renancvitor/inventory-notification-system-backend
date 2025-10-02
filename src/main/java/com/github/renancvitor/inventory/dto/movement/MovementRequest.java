package com.github.renancvitor.inventory.dto.movement;

import java.math.BigDecimal;

public record MovementRequest(
        Integer quantity,
        BigDecimal unitPrice) {

}
