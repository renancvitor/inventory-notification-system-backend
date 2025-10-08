package com.github.renancvitor.inventory.dto.movement;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import com.github.renancvitor.inventory.domain.entity.movement.Movement;
import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;
import com.github.renancvitor.inventory.infra.messaging.systemlog.LoggableData;

public record MovementLogData(
                Long id,
                String productName,
                String movementType,
                Integer quantity,
                BigDecimal unitPrice,
                String movementationDate) implements LoggableData {

        public static MovementLogData fromEntity(Movement movement) {
                String formattedDate = movement.getMovementationDate() != null
                                ? movement.getMovementationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                : null;

                return new MovementLogData(
                                movement.getId(),
                                movement.getProduct().getProductName(),
                                MovementTypeEnum.valueOf(movement.getMovementType().getMovementTypeName())
                                                .getDisplayName(),
                                movement.getQuantity(),
                                movement.getUnitPrice(),
                                formattedDate);
        }
}
