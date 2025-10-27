package com.github.renancvitor.inventory.service.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
import com.github.renancvitor.inventory.application.product.dto.ProductDetailData;
import com.github.renancvitor.inventory.application.product.dto.ProductUpdateData;
import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.application.product.service.ProductService;
import com.github.renancvitor.inventory.application.product.service.StockMonitorService;
import com.github.renancvitor.inventory.application.user.repository.UserTypeRepository;
import com.github.renancvitor.inventory.domain.entity.category.CategoryEntity;
import com.github.renancvitor.inventory.domain.entity.category.enums.CategoryEnum;
import com.github.renancvitor.inventory.domain.entity.person.Person;
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
public class ProductServiceUpdateTests {

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
    private Person person;
    private CategoryEntity categoryEntity;

    @BeforeEach
    void setup() {
        loggedInUser = TestEntityFactory.createUser();
        categoryEntity = TestEntityFactory.createCategory();
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldUpdateProductWithValidData() {
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setId(2);
            categoryEntity.setCategoryName(CategoryEnum.ELECTRONICS.name());

            Product product = TestEntityFactory.createProduct();

            doNothing().when(authenticationService).authorize(any());

            when(productRepository.findByIdAndActiveTrue(anyLong()))
                    .thenReturn(Optional.of(product));
            when(categoryRepository.findById(anyInt()))
                    .thenReturn(Optional.of(categoryEntity));
            when(productRepository.save(any(Product.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            when(productRepository.findByIdAndActiveTrue(anyLong()))
                    .thenReturn(Optional.of(product));

            when(categoryRepository.findById(categoryEntity.getId()))
                    .thenReturn(Optional.of(categoryEntity));

            ProductUpdateData data = new ProductUpdateData(
                    "Produto atualizado",
                    categoryEntity.getId(),
                    BigDecimal.valueOf(50),
                    null,
                    null,
                    null,
                    "Marca atualizada");

            ProductDetailData result = productService.update(product.getId(), data, loggedInUser);

            assertEquals(data.productName(), result.productName());
            assertEquals(CategoryEnum.ELECTRONICS.getDisplayName(), result.category());
            assertEquals(data.price(), result.price());
            assertEquals(data.brand(), result.brand());
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldNotUpdateProductWithNotAuthorizedUser() {
            userTypeEntity = TestEntityFactory.createUserTypeCommon();
            person = TestEntityFactory.createPerson();

            User user = new User();
            user.setPerson(person);
            user.setUserType(userTypeEntity);

            Product product = TestEntityFactory.createProduct();

            ProductUpdateData data = new ProductUpdateData(
                    "Produto atualizado",
                    categoryEntity.getId(),
                    BigDecimal.valueOf(50),
                    null,
                    null,
                    null,
                    "Marca atualizada");

            doThrow(new AuthorizationException(List.of(UserTypeEnum.COMMON.getId())))
                    .when(authenticationService)
                    .authorize(any());

            assertThrows(AuthorizationException.class,
                    () -> productService.update(product.getId(), data, user));

            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        void shouldNotUpdateProductWithCategoryNotFound() {
            ProductUpdateData data = new ProductUpdateData(
                    "Produto atualizado",
                    categoryEntity.getId(),
                    BigDecimal.valueOf(50),
                    null,
                    null,
                    null,
                    "Marca atualizada");

            Product product = TestEntityFactory.createProduct();

            lenient().when(categoryRepository.findById(categoryEntity.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> productService.update(product.getId(), data, loggedInUser));

            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        void shouldNotUpdateProductNotFound() {
            when(productRepository.findByIdAndActiveTrue(anyLong()))
                    .thenReturn(Optional.empty());

            ProductUpdateData data = new ProductUpdateData(
                    "Produto atualizado",
                    categoryEntity.getId(),
                    BigDecimal.valueOf(50),
                    null,
                    null,
                    null,
                    "Marca atualizada");

            Product product = TestEntityFactory.createProduct();

            assertThrows(EntityNotFoundException.class,
                    () -> productService.update(product.getId(), data, loggedInUser));

            verify(productRepository, never()).save(any(Product.class));
        }
    }

}
