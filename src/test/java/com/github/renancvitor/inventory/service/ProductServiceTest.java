package com.github.renancvitor.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
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
import com.github.renancvitor.inventory.application.product.dto.ProductUpdateData;
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
import com.github.renancvitor.inventory.utils.TestEntityFactory;

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

                @BeforeEach
                void setUp() {
                        product = TestEntityFactory.createProduct();
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
                        categoryEntity = TestEntityFactory.createCategory();
                        loggedInUser = TestEntityFactory.createUser();
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
                        userTypeEntity = TestEntityFactory.creaUserTypeAdmin();

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
                        userTypeEntity = TestEntityFactory.creaUserTypeProductManager();

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
                        userTypeEntity = TestEntityFactory.creaUserTypeCommon();

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

        // ======== UPDATE ========
        @Nested
        class UpdateMethosTests {

                private User loggedInUser;
                private UserTypeEntity userTypeEntity;
                private Person person;
                private CategoryEntity categoryEntity;

                @BeforeEach
                void setup() {
                        loggedInUser = TestEntityFactory.createUser();
                        categoryEntity = TestEntityFactory.createCategory();
                }

                // === Caso positivo ===
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

                // === Casos negativos ===
                @Test
                void shouldNotUpdateProductWithNotAuthorizedUser() {
                        userTypeEntity = TestEntityFactory.creaUserTypeCommon();
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
