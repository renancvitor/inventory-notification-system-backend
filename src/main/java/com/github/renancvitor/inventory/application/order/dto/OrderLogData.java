package com.github.renancvitor.inventory.application.order.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.github.renancvitor.inventory.application.movement.dto.MovementLogData;
import com.github.renancvitor.inventory.domain.entity.order.Order;
import com.github.renancvitor.inventory.domain.entity.order.enums.OrderStatusEnum;
import com.github.renancvitor.inventory.infra.messaging.systemlog.LoggableData;

public record OrderLogData(
        Long id,
        String createdAt,
        String description,
        String status,
        String requestBy,
        String approvedBy,
        String rejectedBy,
        List<MovementLogData> movements) implements LoggableData {

    public static OrderLogData fromEntity(Order order) {
        String formattedDate = order.getCreatedAt() != null
                ? order.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : null;

        return new OrderLogData(
                order.getId(),
                formattedDate,
                order.getDescription(),
                OrderStatusEnum.valueOf(order.getOrderStatus().getOrderStatusName()).getDisplayName(),
                order.getRequestedBy() != null ? order.getRequestedBy().getUsername() : null,
                order.getApprovedBy() != null ? order.getApprovedBy().getUsername() : null,
                order.getRejectedBy() != null ? order.getRejectedBy().getUsername() : null,
                order.getMovements() != null
                        ? order.getMovements().stream()
                                .map(MovementLogData::fromEntity)
                                .toList()
                        : List.of());
    }

}
