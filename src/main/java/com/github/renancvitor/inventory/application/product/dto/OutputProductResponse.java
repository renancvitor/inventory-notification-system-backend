package com.github.renancvitor.inventory.application.product.dto;

import com.github.renancvitor.inventory.application.movement.dto.MovementDetailData;

public record OutputProductResponse(
        ProductDetailData product,
        MovementDetailData movement) {

}
