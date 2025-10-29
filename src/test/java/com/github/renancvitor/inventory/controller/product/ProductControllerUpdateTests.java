package com.github.renancvitor.inventory.controller.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.category.repository.CategoryRepository;
import com.github.renancvitor.inventory.application.product.controller.ProductController;
import com.github.renancvitor.inventory.application.product.dto.ProductDetailData;
import com.github.renancvitor.inventory.application.product.dto.ProductUpdateData;
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

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ProductControllerUpdateTests {

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
        void shouldUpdateProductWithValidData() {
            ProductUpdateData data = new ProductUpdateData(
                    "Produto Teste",
                    categoryEntity.getId(),
                    BigDecimal.valueOf(100.00),
                    null,
                    null,
                    15,
                    "Marca teste");

            product.setStock(50);

            ProductDetailData detail = new ProductDetailData(
                    1L,
                    data.productName(),
                    CategoryEnum.valueOf(product.getCategory().getCategoryName()).getDisplayName(),
                    data.price(),
                    null,
                    null,
                    product.getStock(),
                    null,
                    data.brand());

            userTypeEntity = TestEntityFactory.createUserTypeAdmin();
            loggedInUser.setUserType(userTypeEntity);

            when(productService.update(product.getId(), data, loggedInUser))
                    .thenReturn(detail);

            ResponseEntity<ProductDetailData> response = productController.update(
                    product.getId(),
                    data,
                    loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().id()).isEqualTo(1L);

            verify(productService).update(eq(product.getId()), eq(data), eq(loggedInUser));
        }

        @Test
        void shouldUpdateProductWithBodyMapping() {
            ProductUpdateData data = new ProductUpdateData(
                    "Produto Teste",
                    categoryEntity.getId(),
                    BigDecimal.valueOf(100.00),
                    null,
                    null,
                    15,
                    "Marca teste");

            product.setStock(50);

            ProductDetailData detail = new ProductDetailData(
                    1L,
                    data.productName(),
                    CategoryEnum.valueOf(product.getCategory().getCategoryName()).getDisplayName(),
                    data.price(),
                    null,
                    null,
                    product.getStock(),
                    null,
                    data.brand());

            userTypeEntity = TestEntityFactory.createUserTypeAdmin();
            loggedInUser.setUserType(userTypeEntity);

            when(productService.update(product.getId(), data, loggedInUser))
                    .thenReturn(detail);

            ResponseEntity<ProductDetailData> response = productController.update(
                    product.getId(),
                    data,
                    loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(detail);
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldReturn404WhenProductNotFound() {
            Long nonExistentId = 999L;

            when(productService.update(eq(nonExistentId), any(), any()))
                    .thenThrow(new EntityNotFoundException("Produto não encontrado"));

            assertThatThrownBy(() -> productController.update(nonExistentId, null, loggedInUser))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Produto não encontrado");
        }

        @Test
        void shouldFailValidationWhenPriceIsNegativeOrZero() {
            ProductUpdateData data = new ProductUpdateData(
                    "Produto Teste",
                    categoryEntity.getId(),
                    BigDecimal.valueOf(-10.00),
                    null,
                    null,
                    15,
                    "Marca teste");

            Set<ConstraintViolation<ProductUpdateData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("price"));
        }

        @Test
        void shouldReturnInternalServerErrorWhenServiceThrows() {
            ProductUpdateData data = new ProductUpdateData(
                    "Produto Teste",
                    categoryEntity.getId(),
                    BigDecimal.valueOf(100.00),
                    null,
                    null,
                    15,
                    "Marca teste");

            when(productService.update(eq(product.getId()), eq(data), eq(loggedInUser)))
                    .thenThrow(new RuntimeException("Erro inesperado"));

            assertThatThrownBy(() -> productController.update(product.getId(), data, loggedInUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Erro inesperado");
        }
    }

}
