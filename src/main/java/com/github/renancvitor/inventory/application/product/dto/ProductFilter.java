package com.github.renancvitor.inventory.application.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.AssertTrue;

public record ProductFilter(
        Boolean active,
        Integer categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice) {

    @AssertTrue(message = "menor valor não pode ser maior do que o maior valor")
    public boolean isPriceRangeValid() {
        if (minPrice == null || maxPrice == null)
            return true;
        return minPrice.compareTo(maxPrice) <= 0;
    }

}
