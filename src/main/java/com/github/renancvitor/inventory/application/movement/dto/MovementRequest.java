package com.github.renancvitor.inventory.application.movement.dto;

import java.math.BigDecimal;

public record MovementRequest(
                Integer quantity,
                BigDecimal unitPrice) {

}
