package com.github.renancvitor.inventory.service.product;

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
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.category.repository.CategoryRepository;
import com.github.renancvitor.inventory.application.product.dto.ProductListingData;
import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.application.product.service.ProductService;
import com.github.renancvitor.inventory.application.product.service.StockMonitorService;
import com.github.renancvitor.inventory.application.user.repository.UserTypeRepository;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ProductServiceListTests {

        @Mock
        private ProductRepository productRepository;

        @Mock
        private CategoryRepository categoryRepository;

        @Mock
        private UserTypeRepository userTypeRepository;

        @Mock
        private StockMonitorService stockMonitorService;

        @Mock
        private SystemLogPublisherService logPublisherService;

        @Mock
        private AuthenticationService authenticationService;

        @InjectMocks
        private ProductService productService;

        private Product product;

        @BeforeEach
        void setUp() {
                product = TestEntityFactory.createProduct();
        }

        @Nested
        class PositiveCases {
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
        }

        @Nested
        class NegativeCases {

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
