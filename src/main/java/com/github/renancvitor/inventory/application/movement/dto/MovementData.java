package com.github.renancvitor.inventory.application.movement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.github.renancvitor.inventory.application.product.dto.ProductDetailData;

public record MovementData(
                ProductDetailData productDetailData,
                String movementType,
                Integer quantity,
                BigDecimal unitPrice,
                LocalDateTime movementationDate,
                String userName) {

}
