package com.github.renancvitor.inventory.application.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.github.renancvitor.inventory.application.movement.dto.MovementDetailData;
import com.github.renancvitor.inventory.domain.entity.movement.MovementTypeEntity;
import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;
import com.github.renancvitor.inventory.domain.entity.order.Order;
import com.github.renancvitor.inventory.domain.entity.order.enums.OrderStatusEnum;

public record OrderDetailData(
                Long id,
                LocalDateTime createdAt,
                String description,
                BigDecimal totalValue,
                String status,
                String orderType,
                String requestedBy,
                String requestedByName,
                String approvedBy,
                String approvedByName,
                String rejectedBy,
                String rejectedByName,
                List<OrderItemDetailData> items,
                List<MovementDetailData> movements) {
        public OrderDetailData(Order order) {
                this(
                                order.getId(),
                                order.getCreatedAt(),
                                order.getDescription(),
                                order.getTotalValue(),
                                OrderStatusEnum.valueOf(order.getOrderStatus().getOrderStatusName()).getDisplayName(),
                                resolveOrderType(order),
                                order.getRequestedBy() != null ? order.getRequestedBy().getUsername() : null,
                                order.getRequestedBy() != null && order.getRequestedBy().getPerson() != null
                                                ? order.getRequestedBy().getPerson().getPersonName()
                                                : null,
                                order.getApprovedBy() != null ? order.getApprovedBy().getUsername() : null,
                                order.getApprovedBy() != null && order.getApprovedBy().getPerson() != null
                                                ? order.getApprovedBy().getPerson().getPersonName()
                                                : null,
                                order.getRejectedBy() != null ? order.getRejectedBy().getUsername() : null,
                                order.getRejectedBy() != null && order.getRejectedBy().getPerson() != null
                                                ? order.getRejectedBy().getPerson().getPersonName()
                                                : null,
                                order.getItems() != null
                                                ? order.getItems().stream()
                                                                .map(OrderItemDetailData::new)
                                                                .toList()
                                                : List.of(),
                                order.getMovements() != null
                                                ? order.getMovements().stream()
                                                                .map(MovementDetailData::new)
                                                                .toList()
                                                : List.of());
        }

        private static String resolveOrderType(Order order) {
                List<String> orderTypes = new ArrayList<>();

                if (order.getItems() != null) {
                        order.getItems().stream()
                                        .map(item -> item.getMovementType())
                                        .filter(movementType -> movementType != null && movementType.getMovementTypeName() != null)
                                        .map(OrderDetailData::resolveMovementTypeDisplayName)
                                        .forEach(orderTypes::add);
                }

                if (orderTypes.isEmpty() && order.getMovements() != null) {
                        order.getMovements().stream()
                                        .map(movement -> movement.getMovementType())
                                        .filter(movementType -> movementType != null && movementType.getMovementTypeName() != null)
                                        .map(OrderDetailData::resolveMovementTypeDisplayName)
                                        .forEach(orderTypes::add);
                }

                if (orderTypes.isEmpty()) {
                        return null;
                }

                List<String> distinctTypes = orderTypes.stream().distinct().toList();

                return distinctTypes.size() == 1 ? distinctTypes.getFirst() : "Misto";
        }

        private static String resolveMovementTypeDisplayName(MovementTypeEntity movementType) {
                if (movementType.getId() != null) {
                        try {
                                return MovementTypeEnum.fromId(movementType.getId()).getDisplayName();
                        } catch (IllegalArgumentException ignored) {
                                
                        }
                }

                String movementTypeName = movementType.getMovementTypeName();
                for (MovementTypeEnum type : MovementTypeEnum.values()) {
                        if (type.name().equalsIgnoreCase(movementTypeName)
                                        || type.getDisplayName().equalsIgnoreCase(movementTypeName)) {
                                return type.getDisplayName();
                        }
                }

                throw new IllegalArgumentException("Tipo de movimento inválido: " + movementTypeName);
        }
}
