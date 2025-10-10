package com.github.renancvitor.inventory.application.product.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.github.renancvitor.inventory.domain.entity.category.enums.CategoryEnum;
import com.github.renancvitor.inventory.domain.entity.product.Product;

public record ProductListingData(
        Long id,
        String productName,
        String category,
        BigDecimal price,
        LocalDate validity,
        String description,
        Integer stock,
        Integer minimumStrock,
        String brand,
        Boolean active) {
    public ProductListingData(Product product) {
        this(
                product.getId(),
                product.getProductName(),
                CategoryEnum.valueOf(product.getCategory().getCategoryName()).getDisplayName(),
                product.getPrice(),
                product.getValidity(),
                product.getDescription(),
                product.getStock(),
                product.getMinimumStock(),
                product.getBrand(),
                product.getActive());
    }
}
