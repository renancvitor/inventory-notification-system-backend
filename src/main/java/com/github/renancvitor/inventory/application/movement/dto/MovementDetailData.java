package com.github.renancvitor.inventory.application.movement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.github.renancvitor.inventory.domain.entity.movement.Movement;
import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;

public record MovementDetailData(
        Long id,
        String productName,
        String movementType,
        Integer quantity,
        BigDecimal unitPrice,
        LocalDateTime movementationDate,
        String personName) {
    public MovementDetailData(Movement movement) {
        this(
                movement.getId(),
                movement.getProduct().getProductName(),
                MovementTypeEnum.valueOf(movement.getMovementType().getMovementTypeName()).getDisplayName(),
                movement.getQuantity(),
                movement.getUnitPrice(),
                movement.getMovementationDate(),
                movement.getUser().getUsername());
    }

}
