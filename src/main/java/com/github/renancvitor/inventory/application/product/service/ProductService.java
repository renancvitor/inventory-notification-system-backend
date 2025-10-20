package com.github.renancvitor.inventory.application.product.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.category.repository.CategoryRepository;
import com.github.renancvitor.inventory.application.product.dto.ProductCreationData;
import com.github.renancvitor.inventory.application.product.dto.ProductDetailData;
import com.github.renancvitor.inventory.application.product.dto.ProductListingData;
import com.github.renancvitor.inventory.application.product.dto.ProductLogData;
import com.github.renancvitor.inventory.application.product.dto.ProductUpdateData;
import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.application.product.repository.ProductSpecifications;
import com.github.renancvitor.inventory.domain.entity.category.CategoryEntity;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.product.exception.DuplicateProductException;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;
import com.github.renancvitor.inventory.exception.factory.NotFoundExceptionFactory;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

        private final StockMonitorService stockMonitorService;
        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;
        private final SystemLogPublisherService logPublisherService;
        private final AuthenticationService authenticationService;

        public Page<ProductListingData> list(Pageable pageable, Boolean active,
                        Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, User loggedInUser) {
                Specification<Product> specification = Specification.unrestricted();

                if (active != null) {
                        specification = specification.and(ProductSpecifications.active(active));
                }

                if (categoryId != null) {
                        specification = specification.and(ProductSpecifications.categoryId(categoryId));
                }

                if (minPrice != null) {
                        specification = specification.and(ProductSpecifications.minPrice(minPrice));
                }

                if (maxPrice != null) {
                        specification = specification.and(ProductSpecifications.maxPrice(maxPrice));
                }

                return productRepository.findAll(specification, pageable).map(ProductListingData::new);
        }

        @Transactional
        public ProductDetailData create(ProductCreationData data, User loggedInUser) {
                authenticationService.authorize(List.of(UserTypeEnum.ADMIN, UserTypeEnum.PRODUCT_MANAGER));

                CategoryEntity category = categoryRepository.findById(data.categoryId())
                                .orElseThrow(() -> NotFoundExceptionFactory.category(data.categoryId()));

                productRepository.findByProductName(data.productName())
                                .ifPresent(p -> {
                                        throw new DuplicateProductException(data.productName());
                                });

                Product product = new Product(data, category);
                productRepository.save(product);

                ProductLogData newData = ProductLogData.fromEntity(product);

                logPublisherService.publish(
                                "PRODUCT_CREATED",
                                "Produto cadastrado pelo usuário " + loggedInUser.getUsername(),
                                null,
                                newData);

                return new ProductDetailData(product);
        }

        @Transactional
        public ProductDetailData update(Long id, ProductUpdateData data, User loggedInUser) {
                authenticationService.authorize(List.of(UserTypeEnum.ADMIN, UserTypeEnum.PRODUCT_MANAGER));

                Product product = productRepository.findByIdAndActiveTrue(id)
                                .orElseThrow(() -> NotFoundExceptionFactory.product(id));

                ProductLogData oldData = ProductLogData.fromEntity(product);

                CategoryEntity categoryEntity = categoryRepository.findById(data.categoryId())
                                .orElseThrow(() -> NotFoundExceptionFactory.category(data.categoryId()));

                product.setProductName(data.productName());
                product.setCategory(categoryEntity);
                product.setPrice(data.price());
                product.setValidity(data.validity());
                product.setDescription(data.description());
                product.setMinimumStock(data.minimumStock());
                product.setBrand(data.brand());

                stockMonitorService.handleLowStock(product, loggedInUser);

                Product updatedProduct = productRepository.save(product);
                ProductLogData newData = ProductLogData.fromEntity(updatedProduct);

                logPublisherService.publish(
                                "PRODUCT_UPDATED",
                                "Produto editado pelo usuário " + loggedInUser.getUsername(),
                                oldData,
                                newData);

                return new ProductDetailData(updatedProduct);
        }

        @Transactional
        public void delete(Long id, User loggedInUser) {
                authenticationService.authorize(List.of(UserTypeEnum.ADMIN, UserTypeEnum.PRODUCT_MANAGER));

                Product product = productRepository.findByIdAndActiveTrue(id)
                                .orElseThrow(() -> NotFoundExceptionFactory.product(id));

                ProductLogData oldData = ProductLogData.fromEntity(product);

                product.setActive(false);

                Product updatedProduct = productRepository.save(product);
                ProductLogData newData = ProductLogData.fromEntity(updatedProduct);

                logPublisherService.publish(
                                "PRODUCT_DELETED",
                                "Produto inativado (soft delete) pelo usuário " + loggedInUser.getUsername(),
                                oldData,
                                newData);
        }

        @Transactional
        public void activate(Long id, User loggedInUser) {
                authenticationService.authorize(List.of(UserTypeEnum.ADMIN, UserTypeEnum.PRODUCT_MANAGER));

                Product product = productRepository.findByIdAndActiveFalse(id)
                                .orElseThrow(() -> NotFoundExceptionFactory.product(id));

                ProductLogData oldData = ProductLogData.fromEntity(product);

                product.setActive(true);

                Product updatedProduct = productRepository.save(product);
                ProductLogData newData = ProductLogData.fromEntity(updatedProduct);

                logPublisherService.publish(
                                "PRODUCT_ACTIVATED",
                                "Produto reativado (soft restore) pelo usuário " + loggedInUser.getUsername(),
                                oldData,
                                newData);
        }

}
