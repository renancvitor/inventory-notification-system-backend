package com.github.renancvitor.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.github.renancvitor.inventory.domain.entity.product.Product;

public record ProductDetailData(
                Long id,
                String name,
                String category,
                BigDecimal price,
                LocalDate validity,
                String description,
                Integer stock,
                String brand) {

        public ProductDetailData(Product product) {
                this(
                                product.getId(),
                                product.getName(),
                                product.getCategory().getName(),
                                product.getPrice(),
                                product.getValidity(),
                                product.getDescription(),
                                product.getStock(),
                                product.getBrand());
        }

}
