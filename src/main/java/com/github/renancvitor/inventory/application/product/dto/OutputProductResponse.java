package com.github.renancvitor.inventory.application.product.dto;

import com.github.renancvitor.inventory.application.movement.dto.MovementDetailData;
import com.github.renancvitor.inventory.application.order.dto.OrderDetailData;

public record OutputProductResponse(
                ProductDetailData product,
                MovementDetailData movement,
                OrderDetailData order) {

}
