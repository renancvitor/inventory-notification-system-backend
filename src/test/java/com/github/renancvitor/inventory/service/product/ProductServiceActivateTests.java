package com.github.renancvitor.inventory.service.product;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.category.repository.CategoryRepository;
import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.application.product.service.ProductService;
import com.github.renancvitor.inventory.application.product.service.StockMonitorService;
import com.github.renancvitor.inventory.application.user.repository.UserTypeRepository;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;
import com.github.renancvitor.inventory.exception.types.auth.AuthorizationException;
import com.github.renancvitor.inventory.exception.types.common.EntityNotFoundException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ProductServiceActivateTests {

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

    private User loggedInUser;
    private UserTypeEntity userTypeEntity;
    private Product product;

    @BeforeEach
    void setup() {
        loggedInUser = TestEntityFactory.createUser();
        userTypeEntity = TestEntityFactory.createUserTypeAdmin();
        product = TestEntityFactory.createProduct();
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldActivateProductWithUserPermission() {
            loggedInUser.setUserType(userTypeEntity);

            when(productRepository.findByIdAndActiveFalse(product.getId()))
                    .thenReturn(Optional.of(product));

            when(productRepository.save(any(Product.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            productService.activate(product.getId(), loggedInUser);

            assertTrue(product.getActive());
            verify(productRepository).save(product);
            verify(authenticationService)
                    .authorize(List.of(UserTypeEnum.ADMIN, UserTypeEnum.PRODUCT_MANAGER));

        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldNotActivateProductWithNotAuthorizedUser() {
            userTypeEntity = TestEntityFactory.createUserTypeCommon();
            loggedInUser.setUserType(userTypeEntity);

            doThrow(new AuthorizationException(List.of(UserTypeEnum.COMMON.getId())))
                    .when(authenticationService)
                    .authorize(any());

            assertThrows(AuthorizationException.class,
                    () -> productService.activate(product.getId(), loggedInUser));

            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        void shouldNotActivateProductNotFound() {
            when(productRepository.findByIdAndActiveFalse(product.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> productService.activate(product.getId(), loggedInUser));

            verify(productRepository, never()).save(any(Product.class));
        }
    }

}
