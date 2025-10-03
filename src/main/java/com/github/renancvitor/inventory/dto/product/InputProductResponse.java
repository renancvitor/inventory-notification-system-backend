package com.github.renancvitor.inventory.dto.product;

import com.github.renancvitor.inventory.dto.movement.MovementDetailData;

public record InputProductResponse(
        ProductDetailData product,
        MovementDetailData movement) {

}
