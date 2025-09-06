package com.github.renancvitor.inventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.github.renancvitor.inventory.domain.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByActive(Boolean active, Pageable pageable);
}
