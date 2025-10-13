package com.github.renancvitor.inventory.application.order.dto;

import java.util.List;

import com.github.renancvitor.inventory.application.movement.dto.MovementOrderRequest;

public record OrderCreationData(
                String description,
                List<MovementOrderRequest> movements) {

}
