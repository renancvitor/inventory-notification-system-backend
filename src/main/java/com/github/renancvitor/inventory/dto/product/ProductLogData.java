package com.github.renancvitor.inventory.dto.product;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.github.renancvitor.inventory.domain.entity.category.enums.CategoryEnum;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.infra.messaging.systemlog.LoggableData;

public record ProductLogData(
                Long id,
                String productName,
                String category,
                String brand,
                BigDecimal price,
                LocalDate validity) implements LoggableData {
        public static ProductLogData fromEntity(Product product) {
                return new ProductLogData(
                                product.getId(),
                                product.getProductName(),
                                CategoryEnum.valueOf(product.getCategory().getCategoryName()).getDisplayName() != null
                                                ? CategoryEnum.valueOf(product.getCategory().getCategoryName())
                                                                .getDisplayName()
                                                : null,
                                product.getBrand(),
                                product.getPrice(),
                                product.getValidity());
        }
}
