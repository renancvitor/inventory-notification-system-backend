package com.github.renancvitor.inventory.controller.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.category.repository.CategoryRepository;
import com.github.renancvitor.inventory.application.product.controller.ProductController;
import com.github.renancvitor.inventory.application.product.dto.ProductFilter;
import com.github.renancvitor.inventory.application.product.dto.ProductListingData;
import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.application.product.service.ProductService;
import com.github.renancvitor.inventory.application.product.service.StockMonitorService;
import com.github.renancvitor.inventory.application.user.repository.UserTypeRepository;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.CustomPage;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ProductControllerListTests {

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

        @Mock
        private ProductService productService;

        @InjectMocks
        private ProductController productController;

        private Product product;
        private User loggedInUser;

        @BeforeEach
        void setUp() {
                product = TestEntityFactory.createProduct();
                loggedInUser = TestEntityFactory.createUser();
        }

        @Nested
        class PositiveCases {
                @Test
                void shouldCombineMultipleFilters() {
                        product = TestEntityFactory.createProduct();
                        ProductListingData data = new ProductListingData(product);

                        Page<ProductListingData> page = new PageImpl<>(List.of(data),
                                        PageRequest.of(0, 10), 1);

                        when(productService.list(
                                        any(Pageable.class),
                                        any(Boolean.class),
                                        any(Integer.class),
                                        any(BigDecimal.class),
                                        any(BigDecimal.class),
                                        any(User.class)))
                                        .thenReturn(page);

                        ProductFilter filter = new ProductFilter(
                                        true,
                                        1,
                                        BigDecimal.valueOf(10.00),
                                        BigDecimal.valueOf(300.00));

                        ResponseEntity<CustomPage<ProductListingData>> response = productController.list(
                                        PageRequest.of(0, 10), // pageable
                                        loggedInUser, // authentication principal
                                        filter // filter
                        );

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        verify(productService).list(
                                        PageRequest.of(0, 10),
                                        true,
                                        1,
                                        BigDecimal.valueOf(10.00),
                                        BigDecimal.valueOf(300.00),
                                        loggedInUser);
                }

                @Test
                void shouldListWithoutFilters() {
                        product = TestEntityFactory.createProduct();
                        ProductListingData data = new ProductListingData(product);

                        Page<ProductListingData> page = new PageImpl<>(List.of(data),
                                        PageRequest.of(0, 10), 1);

                        when(productService.list(
                                        any(Pageable.class),
                                        isNull(),
                                        isNull(),
                                        isNull(),
                                        isNull(),
                                        any(User.class)))
                                        .thenReturn(page);

                        ProductFilter filter = new ProductFilter(
                                        null,
                                        null,
                                        null,
                                        null);

                        ResponseEntity<CustomPage<ProductListingData>> response = productController.list(
                                        PageRequest.of(0, 10),
                                        loggedInUser,
                                        filter);

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        verify(productService).list(
                                        PageRequest.of(0, 10),
                                        null,
                                        null,
                                        null,
                                        null,
                                        loggedInUser);
                }

                @SuppressWarnings("null")
                @Test
                void shouldReturnEmptyPageWhenNoProductsFound() {
                        Page<ProductListingData> emptyPage = new PageImpl<>(List.of(),
                                        PageRequest.of(0, 10), 0);

                        when(productService.list(any(Pageable.class), any(), any(), any(), any(),
                                        any()))
                                        .thenReturn(emptyPage);

                        ProductFilter filter = new ProductFilter(
                                        true,
                                        1,
                                        BigDecimal.valueOf(10.00),
                                        BigDecimal.valueOf(300.00));

                        ResponseEntity<CustomPage<ProductListingData>> response = productController.list(
                                        PageRequest.of(0, 10),
                                        loggedInUser,
                                        filter);

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody()).isNotNull();
                        assertThat(response.getBody().content()).isEmpty();
                }

                @SuppressWarnings("null")
                @Test
                void shouldApplyCustomPaginationAndSorting() {
                        Pageable customPageable = PageRequest.of(
                                        2,
                                        5,
                                        Sort.by("price").descending());
                        Page<ProductListingData> page = new PageImpl<>(List.of(), customPageable, 0);

                        when(productService.list(
                                        eq(customPageable),
                                        any(),
                                        any(),
                                        any(),
                                        any(),
                                        any()))
                                        .thenReturn(page);

                        ProductFilter filter = new ProductFilter(
                                        null,
                                        null,
                                        null,
                                        null);

                        ResponseEntity<CustomPage<ProductListingData>> response = productController.list(
                                        customPageable,
                                        loggedInUser,
                                        filter);

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody().page()).isEqualTo(2);
                        assertThat(response.getBody().size()).isEqualTo(5);
                        assertThat(response.getBody().totalElements()).isEqualTo(0L);

                        verify(productService).list(
                                        eq(customPageable),
                                        any(),
                                        any(),
                                        any(),
                                        any(),
                                        any());
                }
        }
}