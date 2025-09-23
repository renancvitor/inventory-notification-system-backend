package com.github.renancvitor.inventory.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.renancvitor.inventory.domain.entity.category.CategoryEntity;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.enums.user.UserTypeEnum;
import com.github.renancvitor.inventory.dto.product.ProductCreationData;
import com.github.renancvitor.inventory.dto.product.ProductDetailData;
import com.github.renancvitor.inventory.dto.product.ProductListingData;
import com.github.renancvitor.inventory.exception.factory.NotFoundExceptionFactory;
import com.github.renancvitor.inventory.exception.types.auth.AuthorizationException;
import com.github.renancvitor.inventory.exception.types.product.DuplicateProductException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.repository.CategoryRepository;
import com.github.renancvitor.inventory.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SystemLogPublisherService logPublisherService;

    public ProductService(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            SystemLogPublisherService logPublisherService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.logPublisherService = logPublisherService;
    }

    public Page<ProductListingData> list(Pageable pageable, Boolean active,
            Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, User loggedInUser) {
        Page<Product> page;

        if (active != null && categoryId != null && minPrice != null && maxPrice != null) {
            page = productRepository.findByActiveAndCategoryIdAndPriceBetween(active, categoryId, minPrice, maxPrice,
                    pageable);
        } else if (active != null && categoryId != null) {
            page = productRepository.findByActiveAndCategoryId(active, categoryId, pageable);
        } else if (active != null) {
            page = productRepository.findByActive(active, pageable);
        } else if (categoryId != null) {
            page = productRepository.findByCategoryId(categoryId, pageable);
        } else {
            page = productRepository.findAll(pageable);
        }

        return page.map(ProductListingData::new);
    }

    @Transactional
    public ProductDetailData create(ProductCreationData data, User loggedInUser) {
        List<Integer> allowedTypeIds = List.of(
                UserTypeEnum.ADMIN.getId(),
                UserTypeEnum.PRODUCT_MANAGER.getId());

        boolean authorized = allowedTypeIds.contains(loggedInUser.getUserType().getId());

        if (!authorized) {
            throw new AuthorizationException(allowedTypeIds);
        }

        CategoryEntity category = categoryRepository.findById(data.categoryId())
                .orElseThrow(() -> NotFoundExceptionFactory.category(data.categoryId()));

        productRepository.findByProductName(data.productName())
                .ifPresent(p -> {
                    throw new DuplicateProductException(data.productName());
                });

        Product product = new Product(data, category);
        productRepository.save(product);

        logPublisherService.publish(
                "PRODUCT_CREATED",
                "Produto cadastrado pelo usuário " + loggedInUser.getUsername(),
                "N/A",
                product.getProductName());

        return new ProductDetailData(product);
    }

}
