package com.github.renancvitor.inventory.controller.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.application.product.service.ProductService;
import com.github.renancvitor.inventory.application.product.service.StockMonitorService;
import com.github.renancvitor.inventory.application.user.repository.UserTypeRepository;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ProductControllerActivateTests {

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
    private UserTypeEntity userTypeEntity;
    private Product product;

    @BeforeEach
    void setUp() {
        loggedInUser = TestEntityFactory.createUser();
        product = TestEntityFactory.createProduct();
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldActivateProductWithUserPermission() {
            userTypeEntity = TestEntityFactory.createUserTypeAdmin();
            loggedInUser.setUserType(userTypeEntity);

            ResponseEntity<Void> response = productController.activate(product.getId(), loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(productService).activate(product.getId(), loggedInUser);
        }

        @Test
        void shouldReturnNoContentWhenActivateCalledTwice() {
            ResponseEntity<Void> responseOne = productController.activate(product.getId(), loggedInUser);
            assertThat(responseOne.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            ResponseEntity<Void> responseTwo = productController.activate(product.getId(), loggedInUser);
            assertThat(responseTwo.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            verify(productService, times(2)).activate(product.getId(), loggedInUser);
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldReturn404WhenProductNotFound() {
            Long nonExistentId = 999L;

            doThrow(new EntityNotFoundException("Produto não encontrado"))
                    .when(productService).activate(eq(nonExistentId), any());

            assertThatThrownBy(() -> productController.activate(nonExistentId, loggedInUser))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Produto não encontrado");
        }

        @Test
        void shouldReturnInternalServerErrorWhenServiceThrows() {
            doThrow(new RuntimeException("Erro inesperado"))
                    .when(productService).activate(eq(product.getId()), eq(loggedInUser));

            assertThatThrownBy(() -> productController.activate(product.getId(), loggedInUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Erro inesperado");
        }
    }

}
