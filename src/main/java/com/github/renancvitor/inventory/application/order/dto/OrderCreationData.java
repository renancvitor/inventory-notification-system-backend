package com.github.renancvitor.inventory.application.order.dto;

import java.util.List;

public record OrderCreationData(
        String description,
        List<OrderItemRequest> items) {

}
