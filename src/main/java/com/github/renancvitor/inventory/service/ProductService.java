package com.github.renancvitor.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.renancvitor.inventory.domain.entity.category.CategoryEntity;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.dto.ProductCreationData;
import com.github.renancvitor.inventory.dto.ProductDetailData;
import com.github.renancvitor.inventory.dto.ProductListingData;
import com.github.renancvitor.inventory.repository.CategoryRepository;
import com.github.renancvitor.inventory.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Transactional
    public Page<ProductListingData> list(Pageable pageable, Boolean active) {
        if (active != null) {
            return productRepository.findAllByActive(active, pageable).map(ProductListingData::new);
        }

        return productRepository.findAll(pageable).map(ProductListingData::new);
    }

    @Transactional
    public ProductDetailData create(ProductCreationData data) {
        CategoryEntity category = categoryRepository.findById(data.categoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada."));

        Product product = new Product(data, category);

        productRepository.save(product);
        return new ProductDetailData(product);
    }

}
