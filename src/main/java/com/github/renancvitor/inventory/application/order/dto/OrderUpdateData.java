package com.github.renancvitor.inventory.application.order.dto;

import java.util.List;

import com.github.renancvitor.inventory.application.movement.dto.MovementOrderRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record OrderUpdateData(
                String description,
                @Valid @NotNull(message = "A lista de movimentações não pode ser nula") List<MovementOrderRequest> movements) {

}
