package com.github.renancvitor.inventory.application.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.github.renancvitor.inventory.application.movement.dto.MovementDetailData;
import com.github.renancvitor.inventory.domain.entity.order.Order;
import com.github.renancvitor.inventory.domain.entity.order.enums.OrderStatusEnum;

public record OrderDetailData(
        Long id,
        LocalDateTime createdAt,
        String description,
        BigDecimal totalValue,
        String status,
        String requestedBy,
        String approvedBy,
        String rejectedBy,
        List<MovementDetailData> movements) {
    public OrderDetailData(Order order) {
        this(
                order.getId(),
                order.getCreatedAt(),
                order.getDescription(),
                order.getTotalValue(),
                OrderStatusEnum.valueOf(order.getOrderStatus().getOrderStatusName()).getDisplayName() != null
                        ? order.getRequestedBy().getUsername()
                        : null,
                order.getRequestedBy() != null ? order.getRequestedBy().getUsername() : null,
                order.getApprovedBy() != null ? order.getApprovedBy().getUsername() : null,
                order.getRejectedBy() != null ? order.getRejectedBy().getUsername() : null,
                order.getMovements() != null
                        ? order.getMovements().stream()
                                .map(MovementDetailData::new)
                                .toList()
                        : List.of());
    }
}
