package com.github.renancvitor.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
import com.github.renancvitor.inventory.application.product.dto.ProductCreationData;
import com.github.renancvitor.inventory.application.product.dto.ProductDetailData;
import com.github.renancvitor.inventory.application.product.dto.ProductListingData;
import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.application.product.service.ProductService;
import com.github.renancvitor.inventory.application.product.service.StockMonitorService;
import com.github.renancvitor.inventory.application.user.repository.UserTypeRepository;
import com.github.renancvitor.inventory.domain.entity.category.CategoryEntity;
import com.github.renancvitor.inventory.domain.entity.category.enums.CategoryEnum;
import com.github.renancvitor.inventory.domain.entity.person.Person;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.product.exception.DuplicateProductException;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;
import com.github.renancvitor.inventory.exception.types.auth.AuthorizationException;
import com.github.renancvitor.inventory.exception.types.common.EntityNotFoundException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ProductServiceTest {

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

        // ======== CREATE ========
        @Nested
        class CreateMethodsTests {

                private CategoryEntity categoryEntity;
                private User loggedInUser;
                private UserTypeEntity userTypeEntity;

                @BeforeEach
                void setUp() {
                        categoryEntity = new CategoryEntity();
                        categoryEntity.setId(1);
                        categoryEntity.setCategoryName(CategoryEnum.OTHERS.name());

                        Person person = new Person();
                        person.setEmail("teste@exemplo.com");
                        person.setPersonName("Usuário Teste");

                        loggedInUser = new User();
                        loggedInUser.setPerson(person);
                }

                // === Casos positivos ===
                @Test
                void shouldCreateProductWithValidData() {
                        ProductCreationData data = new ProductCreationData(
                                        "Produto teste",
                                        categoryEntity.getId(),
                                        BigDecimal.valueOf(100.00),
                                        null,
                                        null,
                                        100,
                                        null,
                                        "Marca teste");

                        when(categoryRepository.findById(categoryEntity.getId()))
                                        .thenReturn(Optional.of(categoryEntity));

                        ProductDetailData result = productService.create(data, loggedInUser);

                        assertNotNull(result);
                        assertEquals("Produto teste", result.productName());
                        assertEquals("Marca teste", result.brand());
                        assertEquals(BigDecimal.valueOf(100.00), result.price());

                        verify(productRepository, times(1)).save(any(Product.class));
                }

                @Test
                void shouldCreateProductWithAdminUser() {
                        userTypeEntity = new UserTypeEntity();
                        userTypeEntity.setId(1);
                        userTypeEntity.setUserTypeName(UserTypeEnum.ADMIN.name());

                        loggedInUser.setUserType(userTypeEntity);

                        ProductCreationData data = new ProductCreationData(
                                        "Produto teste",
                                        categoryEntity.getId(),
                                        BigDecimal.valueOf(100.00),
                                        null,
                                        null,
                                        100,
                                        null,
                                        "Marca teste");

                        when(categoryRepository.findById(categoryEntity.getId()))
                                        .thenReturn(Optional.of(categoryEntity));

                        ProductDetailData result = productService.create(data, loggedInUser);

                        assertNotNull(result);
                        assertEquals(UserTypeEnum.ADMIN.name(), loggedInUser.getUserType().getUserTypeName());

                        verify(productRepository, times(1)).save(any(Product.class));
                }

                @Test
                void shouldCreateProductWithProductManagerUser() {
                        userTypeEntity = new UserTypeEntity();
                        userTypeEntity.setId(1);
                        userTypeEntity.setUserTypeName(UserTypeEnum.PRODUCT_MANAGER.name());

                        loggedInUser.setUserType(userTypeEntity);

                        ProductCreationData data = new ProductCreationData(
                                        "Produto teste",
                                        categoryEntity.getId(),
                                        BigDecimal.valueOf(100.00),
                                        null,
                                        null,
                                        100,
                                        null,
                                        "Marca teste");

                        when(categoryRepository.findById(categoryEntity.getId()))
                                        .thenReturn(Optional.of(categoryEntity));

                        ProductDetailData result = productService.create(data, loggedInUser);

                        assertNotNull(result);
                        assertEquals(UserTypeEnum.PRODUCT_MANAGER.name(), loggedInUser.getUserType().getUserTypeName());

                        verify(productRepository, times(1)).save(any(Product.class));
                }

                // === Casos negativos ===
                @Test
                void shouldNotCreateProductWithNotAuthorizedUser() {
                        userTypeEntity = new UserTypeEntity();
                        userTypeEntity.setId(3);
                        userTypeEntity.setUserTypeName(UserTypeEnum.COMMON.name());

                        Person person = new Person();
                        person.setEmail("teste@exemplo.com");
                        person.setPersonName("Usuário Teste");

                        loggedInUser.setPerson(person);
                        loggedInUser.setUserType(userTypeEntity);

                        ProductCreationData data = new ProductCreationData(
                                        "Produto teste",
                                        categoryEntity.getId(),
                                        BigDecimal.valueOf(100.00),
                                        null,
                                        null,
                                        100,
                                        null,
                                        "Marca teste");

                        doThrow(new AuthorizationException(List.of(UserTypeEnum.COMMON.getId())))
                                        .when(authenticationService)
                                        .authorize(any());

                        assertThrows(AuthorizationException.class, () -> productService.create(data, loggedInUser));

                        verify(productRepository, never()).save(any(Product.class));
                }

                @Test
                void shouldNotCreateProductWithCategoryNotFound() {
                        ProductCreationData data = new ProductCreationData(
                                        "Produto teste",
                                        categoryEntity.getId(),
                                        BigDecimal.valueOf(100.00),
                                        null,
                                        null,
                                        100,
                                        null,
                                        "Marca teste");

                        when(categoryRepository.findById(categoryEntity.getId()))
                                        .thenReturn(Optional.empty());

                        assertThrows(EntityNotFoundException.class,
                                        () -> productService.create(data, loggedInUser));

                        verify(productRepository, never()).save(any(Product.class));
                }

                @Test
                void shouldNotCreateProductWithDuplicatedName() {
                        ProductCreationData data = new ProductCreationData(
                                        "Produto teste",
                                        categoryEntity.getId(),
                                        BigDecimal.valueOf(100.00),
                                        null,
                                        null,
                                        100,
                                        null,
                                        "Marca teste");

                        when(categoryRepository.findById(categoryEntity.getId()))
                                        .thenReturn(Optional.of(categoryEntity));

                        Product existingProduct = new Product();
                        existingProduct.setProductName("Produto teste");

                        when(productRepository.findByProductName(data.productName()))
                                        .thenReturn(Optional.of(existingProduct));

                        assertThrows(DuplicateProductException.class,
                                        () -> productService.create(data, loggedInUser));

                        verify(productRepository, never()).save(any(Product.class));
                }
        }

}
