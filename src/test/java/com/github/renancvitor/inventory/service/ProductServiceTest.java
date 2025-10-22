package com.github.renancvitor.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.category.repository.CategoryRepository;
import com.github.renancvitor.inventory.application.product.dto.ProductListingData;
import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.application.product.service.ProductService;
import com.github.renancvitor.inventory.application.product.service.StockMonitorService;
import com.github.renancvitor.inventory.domain.entity.category.CategoryEntity;
import com.github.renancvitor.inventory.domain.entity.category.enums.CategoryEnum;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

        @Mock
        private ProductRepository productRepository;

        @Mock
        private CategoryRepository categoryRepository;

        @Mock
        private StockMonitorService stockMonitorService;

        @Mock
        private SystemLogPublisherService logPublisherService;

        @Mock
        private AuthenticationService authenticationService;

        @InjectMocks
        private ProductService productService;

        // ======== LIST ========
        @Nested
        class ListMethodsTests {

                private Product product;
                private CategoryEntity categoryEntity;

                @BeforeEach
                void setUp() {
                        categoryEntity = new CategoryEntity();
                        categoryEntity.setId(1);
                        categoryEntity.setCategoryName(CategoryEnum.OTHERS.name());

                        product = new Product();
                        product.setId(1L);
                        product.setProductName("Produto Teste");
                        product.setPrice(BigDecimal.valueOf(100));
                        product.setCategory(categoryEntity);
                }

                // === Casos positivos ===
                @Test
                void shouldListAllProducts() {
                        Page<Product> page = new PageImpl<>(List.of(product));
                        when(productRepository.findAll(
                                        ArgumentMatchers.<Specification<Product>>any(),
                                        any(Pageable.class))).thenReturn(page);

                        Page<ProductListingData> result = productService.list(
                                        PageRequest.of(0, 10),
                                        null, null, null, null, null);

                        assertEquals(1, result.getTotalElements());

                        verify(productRepository)
                                        .findAll(ArgumentMatchers.<Specification<Product>>any(),
                                                        eq(PageRequest.of(0, 10)));

                }

                @Test
                void shouldFilterByCategoryId() {
                        Page<Product> page = new PageImpl<>(List.of(product));
                        when(productRepository.findAll(
                                        ArgumentMatchers.<Specification<Product>>any(),
                                        any(Pageable.class))).thenReturn(page);

                        Page<ProductListingData> result = productService.list(
                                        PageRequest.of(0, 10),
                                        null,
                                        1,
                                        null,
                                        null,
                                        null);

                        assertEquals(1, result.getTotalElements());

                        verify(productRepository)
                                        .findAll(ArgumentMatchers.<Specification<Product>>any(),
                                                        eq(PageRequest.of(0, 10)));
                }

                @Test
                void shouldFilterByPriceMinMax() {
                        Page<Product> page = new PageImpl<>(List.of(product));
                        when(productRepository.findAll(
                                        ArgumentMatchers.<Specification<Product>>any(),
                                        any(Pageable.class))).thenReturn(page);

                        Page<ProductListingData> result = productService.list(
                                        PageRequest.of(0, 10),
                                        null,
                                        null,
                                        BigDecimal.valueOf(10.00),
                                        BigDecimal.valueOf(300.00),
                                        null);

                        assertEquals(1, result.getTotalElements());

                        verify(productRepository)
                                        .findAll(ArgumentMatchers.<Specification<Product>>any(),
                                                        eq(PageRequest.of(0, 10)));
                }

                @Test
                void shouldFilterByActiveStatus() {
                        Page<Product> page = new PageImpl<>(List.of(product));
                        when(productRepository.findAll(
                                        ArgumentMatchers.<Specification<Product>>any(),
                                        any(Pageable.class))).thenReturn(page);

                        Page<ProductListingData> result = productService.list(
                                        PageRequest.of(0, 10),
                                        true,
                                        null,
                                        null,
                                        null,
                                        null);

                        assertEquals(1, result.getTotalElements());

                        verify(productRepository)
                                        .findAll(ArgumentMatchers.<Specification<Product>>any(),
                                                        eq(PageRequest.of(0, 10)));
                }

                @Test
                void shouldCombineMultipleFilters() {
                        Page<Product> page = new PageImpl<>(List.of(product));
                        when(productRepository.findAll(
                                        ArgumentMatchers.<Specification<Product>>any(),
                                        any(Pageable.class))).thenReturn(page);

                        Page<ProductListingData> result = productService.list(
                                        PageRequest.of(0, 10),
                                        true,
                                        1,
                                        BigDecimal.valueOf(10.00),
                                        BigDecimal.valueOf(300.00),
                                        null);

                        assertEquals(1, result.getTotalElements());

                        verify(productRepository)
                                        .findAll(ArgumentMatchers.<Specification<Product>>any(),
                                                        eq(PageRequest.of(0, 10)));
                }

                // === Casos negativos ===
                @Test
                void shouldReturnEmptyPageWhenNoProductsFound() {
                        when(productRepository.findAll(
                                        ArgumentMatchers.<Specification<Product>>any(),
                                        any(Pageable.class))).thenReturn(Page.empty());

                        Page<ProductListingData> result = productService.list(
                                        PageRequest.of(0, 10),
                                        null, null, null, null, null);

                        assertTrue(result.isEmpty());
                        assertEquals(0, result.getTotalElements());

                        verify(productRepository)
                                        .findAll(ArgumentMatchers.<Specification<Product>>any(),
                                                        eq(PageRequest.of(0, 10)));
                }

                @Test
                void shouldIgnoreFiltersWhenAllAreNull() {
                        Page<Product> page = new PageImpl<>(List.of(product));
                        when(productRepository.findAll(
                                        ArgumentMatchers.<Specification<Product>>any(),
                                        any(Pageable.class))).thenReturn(page);

                        Page<ProductListingData> result = productService.list(
                                        PageRequest.of(0, 10),
                                        null, null, null, null, null);

                        assertNotNull(result);
                        assertEquals(1, result.getTotalElements());

                        verify(productRepository)
                                        .findAll(ArgumentMatchers.<Specification<Product>>any(),
                                                        eq(PageRequest.of(0, 10)));
                }
        }

}
