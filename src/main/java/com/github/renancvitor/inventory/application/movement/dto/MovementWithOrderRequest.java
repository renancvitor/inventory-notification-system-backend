package com.github.renancvitor.inventory.application.movement.dto;

import com.github.renancvitor.inventory.application.order.dto.OrderCreationData;

import jakarta.validation.Valid;

public record MovementWithOrderRequest(
        @Valid MovementOrderRequest movement,
        @Valid OrderCreationData order) {

}
