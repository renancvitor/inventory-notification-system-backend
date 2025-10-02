package com.github.renancvitor.inventory.dto.movement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.github.renancvitor.inventory.dto.product.ProductDetailData;

public record MovementData(
                ProductDetailData productDetailData,
                String movementType,
                Integer quantity,
                BigDecimal unitPrice,
                LocalDateTime movementationDate,
                String userName) {

}
