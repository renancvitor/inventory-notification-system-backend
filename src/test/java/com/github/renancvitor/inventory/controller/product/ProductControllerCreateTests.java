package com.github.renancvitor.inventory.controller.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.category.repository.CategoryRepository;
import com.github.renancvitor.inventory.application.product.controller.ProductController;
import com.github.renancvitor.inventory.application.product.dto.ProductCreationData;
import com.github.renancvitor.inventory.application.product.dto.ProductDetailData;
import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.application.product.service.ProductService;
import com.github.renancvitor.inventory.application.product.service.StockMonitorService;
import com.github.renancvitor.inventory.application.user.repository.UserTypeRepository;
import com.github.renancvitor.inventory.domain.entity.category.CategoryEntity;
import com.github.renancvitor.inventory.domain.entity.category.enums.CategoryEnum;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ProductControllerCreateTests {

    private Validator validator;

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

    private User loggedInUser;
    private CategoryEntity categoryEntity;
    private UserTypeEntity userTypeEntity;
    private Product product;

    @BeforeEach
    void setUp() {
        loggedInUser = TestEntityFactory.createUser();
        categoryEntity = TestEntityFactory.createCategory();
        product = TestEntityFactory.createProduct();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    class PositiveCases {
        @SuppressWarnings("null")
        @Test
        void shouldCreateProductWithValidData() {
            ProductCreationData data = new ProductCreationData(
                    "Produto Teste",
                    categoryEntity.getId(),
                    BigDecimal.valueOf(100.00),
                    null,
                    null,
                    100,
                    null,
                    "Marca teste");

            ProductDetailData detail = new ProductDetailData(
                    1L,
                    data.productName(),
                    CategoryEnum.valueOf(product.getCategory().getCategoryName()).getDisplayName(),
                    data.price(),
                    null,
                    null,
                    data.stock(),
                    null,
                    data.brand());

            userTypeEntity = TestEntityFactory.createUserTypeAdmin();
            loggedInUser.setUserType(userTypeEntity);

            when(productService.create(eq(data), eq(loggedInUser)))
                    .thenReturn(detail);

            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

            ResponseEntity<ProductDetailData> response = productController.create(
                    data,
                    uriComponentsBuilder,
                    loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getHeaders().getLocation())
                    .isEqualTo(URI.create("http://localhost/products/" + 1L));
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().id()).isEqualTo(1L);
            assertThat(response.getBody().productName()).isEqualTo("Produto Teste");

            verify(productService).create(eq(data), eq(loggedInUser));
        }

        @Test
        void shouldCreateProductWithBodyMapping() {
            ProductCreationData data = new ProductCreationData(
                    "Produto Teste",
                    categoryEntity.getId(),
                    BigDecimal.valueOf(100.00),
                    null,
                    null,
                    100,
                    null,
                    "Marca teste");

            ProductDetailData detail = new ProductDetailData(
                    1L,
                    data.productName(),
                    CategoryEnum.valueOf(product.getCategory().getCategoryName()).getDisplayName(),
                    data.price(),
                    null,
                    null,
                    data.stock(),
                    null,
                    data.brand());

            userTypeEntity = TestEntityFactory.createUserTypeAdmin();
            loggedInUser.setUserType(userTypeEntity);

            when(productService.create(eq(data), eq(loggedInUser)))
                    .thenReturn(detail);

            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

            ResponseEntity<ProductDetailData> response = productController.create(
                    data,
                    uriComponentsBuilder,
                    loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(detail);
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldReturnBadRequestWhenProductNameIsNull() {
            ProductCreationData data = new ProductCreationData(
                    null,
                    1,
                    BigDecimal.valueOf(100),
                    null,
                    null,
                    10,
                    null,
                    "Marca teste");

            Set<ConstraintViolation<ProductCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("productName"));
        }

        @Test
        void shouldReturnBadRequestWhenCategoryIdIsNull() {
            ProductCreationData data = new ProductCreationData(
                    "Produto Teste",
                    null,
                    BigDecimal.valueOf(100),
                    null,
                    null,
                    10,
                    null,
                    "Marca teste");

            Set<ConstraintViolation<ProductCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("categoryId"));
        }

        @Test
        void shouldReturnBadRequestWhenPriceIsNull() {
            ProductCreationData data = new ProductCreationData(
                    "Produto Teste",
                    1,
                    null,
                    null,
                    null,
                    10,
                    null,
                    "Marca teste");

            Set<ConstraintViolation<ProductCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("price"));
        }

        @Test
        void shouldFailValidationWhenPriceIsNegativeOrZero() {
            ProductCreationData data = new ProductCreationData(
                    "Produto Teste",
                    1,
                    BigDecimal.valueOf(-10.00),
                    null,
                    null,
                    10,
                    null,
                    "Marca teste");

            Set<ConstraintViolation<ProductCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("price"));
        }

        @Test
        void shouldReturnBadRequestWhenValidityIsPast() {
            ProductCreationData data = new ProductCreationData(
                    "Produto Teste",
                    1,
                    BigDecimal.valueOf(10.00),
                    LocalDate.now().minusDays(1),
                    null,
                    10,
                    null,
                    "Marca teste");

            Set<ConstraintViolation<ProductCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("validity"));
        }

        @Test
        void shouldReturnBadRequestWhenStockIsNull() {
            ProductCreationData data = new ProductCreationData(
                    "Produto Teste",
                    1,
                    BigDecimal.valueOf(10.00),
                    null,
                    null,
                    null,
                    null,
                    "Marca teste");

            Set<ConstraintViolation<ProductCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("stock"));
        }

        @Test
        void shouldReturnBadRequestWhenBrandIsNull() {
            ProductCreationData data = new ProductCreationData(
                    "Produto Teste",
                    1,
                    BigDecimal.valueOf(10.00),
                    null,
                    null,
                    100,
                    null,
                    null);

            Set<ConstraintViolation<ProductCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("brand"));
        }

        @Test
        void shouldReturnInternalServerErrorWhenServiceThrows() {
            ProductCreationData data = new ProductCreationData(
                    "Produto Teste",
                    1,
                    BigDecimal.valueOf(100),
                    null,
                    null,
                    10,
                    null,
                    "Marca teste");

            when(productService.create(eq(data), eq(loggedInUser)))
                    .thenThrow(new RuntimeException("Erro inesperado"));

            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

            assertThatThrownBy(() -> productController.create(data, uriComponentsBuilder, loggedInUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Erro inesperado");
        }
    }

}
