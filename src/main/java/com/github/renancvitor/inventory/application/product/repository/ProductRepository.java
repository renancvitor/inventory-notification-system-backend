package com.github.renancvitor.inventory.application.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.github.renancvitor.inventory.domain.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByIdAndActiveTrue(Long id);

    Optional<Product> findByIdAndActiveFalse(Long id);

    Optional<Product> findByProductName(String productName);

}
