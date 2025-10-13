package com.github.renancvitor.inventory.application.product.dto;

import com.github.renancvitor.inventory.application.movement.dto.MovementDetailData;
import com.github.renancvitor.inventory.application.order.dto.OrderDetailData;

public record InputProductResponse(
                ProductDetailData product,
                MovementDetailData movement,
                OrderDetailData order) {

}
