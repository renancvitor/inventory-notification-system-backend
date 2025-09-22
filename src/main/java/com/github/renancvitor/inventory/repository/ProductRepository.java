package com.github.renancvitor.inventory.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.github.renancvitor.inventory.domain.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByActive(Boolean active, Pageable pageable);

    Page<Product> findByActiveAndCategoryId(Boolean active, Integer categoryId, Pageable pageable);

    Page<Product> findAllByActive(Boolean active, Pageable pageable);

    Optional<Product> findByName(String productName);

    Page<Product> findByActiveAndCategoryIdAndPriceBetween(
            Boolean active, Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByCategoryId(Integer categoryId, Pageable pageable);

}
