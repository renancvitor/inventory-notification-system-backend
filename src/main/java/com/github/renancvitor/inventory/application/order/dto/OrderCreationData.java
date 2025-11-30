package com.github.renancvitor.inventory.application.order.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record OrderCreationData(
                @NotBlank String description,
                @NotEmpty @Valid List<OrderItemRequest> items) {

}
