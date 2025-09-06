package com.github.renancvitor.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.renancvitor.inventory.dto.ProductListingData;
import com.github.renancvitor.inventory.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Transactional
    public Page<ProductListingData> list(Pageable pageable, Boolean active) {
        if (active != null) {
            return productRepository.findAllByActive(active, pageable).map(ProductListingData::new);
        }

        return productRepository.findAll(pageable).map(ProductListingData::new);
    }

}
