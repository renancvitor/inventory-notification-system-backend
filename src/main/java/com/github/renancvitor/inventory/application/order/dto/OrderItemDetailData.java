package com.github.renancvitor.inventory.application.order.dto;

import java.math.BigDecimal;

import com.github.renancvitor.inventory.domain.entity.movement.MovementTypeEntity;
import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;
import com.github.renancvitor.inventory.domain.entity.order.OrderItem;

public record OrderItemDetailData(
        Long productId,
        String productName,
        Integer movementTypeId,
        String movementType,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalValue) {

    public OrderItemDetailData(OrderItem item) {
        this(
                item.getProduct() != null ? item.getProduct().getId() : null,
                item.getProduct() != null ? item.getProduct().getProductName() : null,
                item.getMovementType() != null ? item.getMovementType().getId() : null,
                resolveMovementTypeDisplayName(item.getMovementType()),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotal());
    }

    private static String resolveMovementTypeDisplayName(MovementTypeEntity movementType) {
        if (movementType == null) {
            return null;
        }

        if (movementType.getId() != null) {
            try {
                return MovementTypeEnum.fromId(movementType.getId()).getDisplayName();
            } catch (IllegalArgumentException ignored) {
                // Fall back to name-based resolution.
            }
        }

        String movementTypeName = movementType.getMovementTypeName();
        if (movementTypeName == null) {
            return null;
        }

        for (MovementTypeEnum type : MovementTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(movementTypeName)
                    || type.getDisplayName().equalsIgnoreCase(movementTypeName)) {
                return type.getDisplayName();
            }
        }

        return movementTypeName;
    }

}
