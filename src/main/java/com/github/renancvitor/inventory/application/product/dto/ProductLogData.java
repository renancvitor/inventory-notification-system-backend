package com.github.renancvitor.inventory.application.product.dto;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import com.github.renancvitor.inventory.domain.entity.category.enums.CategoryEnum;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.infra.messaging.systemlog.LoggableData;

public record ProductLogData(
                Long id,
                String productName,
                String category,
                String brand,
                BigDecimal price,
                String validity) implements LoggableData {
        public static ProductLogData fromEntity(Product product) {
                String formattedDate = product.getValidity() != null
                                ? product.getValidity().format(DateTimeFormatter.ISO_LOCAL_DATE)
                                : null;

                return new ProductLogData(
                                product.getId(),
                                product.getProductName(),
                                CategoryEnum.valueOf(product.getCategory().getCategoryName()).getDisplayName() != null
                                                ? CategoryEnum.valueOf(product.getCategory().getCategoryName())
                                                                .getDisplayName()
                                                : null,
                                product.getBrand(),
                                product.getPrice(),
                                formattedDate);
        }
}
