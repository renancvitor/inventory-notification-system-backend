package com.github.renancvitor.inventory.dto.product;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.github.renancvitor.inventory.domain.entity.product.Product;

public record ProductListingData(
        Long id,
        String productName,
        String category,
        BigDecimal price,
        LocalDate validity,
        String description,
        Integer stock,
        String brand,
        Boolean active) {
    public ProductListingData(Product product) {
        this(
                product.getId(),
                product.getProductName(),
                product.getCategory().getCategoryName(),
                product.getPrice(),
                product.getValidity(),
                product.getDescription(),
                product.getStock(),
                product.getBrand(),
                product.getActive());
    }
}
