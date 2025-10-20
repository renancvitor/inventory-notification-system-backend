package com.github.renancvitor.inventory.application.product.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.github.renancvitor.inventory.domain.entity.product.Product;

public class ProductSpecifications {

    public static Specification<Product> active(Boolean active) {
        return (root, query, builder) -> builder.equal(root.get("active"), active);
    }

    public static Specification<Product> categoryId(Integer categoryId) {
        return (root, query, builder) -> builder.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> minPrice(BigDecimal minPrice) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> maxPrice(BigDecimal maxPrice) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

}
