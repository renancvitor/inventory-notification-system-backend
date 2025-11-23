package com.github.renancvitor.inventory.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.github.renancvitor.inventory.domain.entity.category.CategoryEntity;
import com.github.renancvitor.inventory.domain.entity.category.enums.CategoryEnum;
import com.github.renancvitor.inventory.domain.entity.movement.Movement;
import com.github.renancvitor.inventory.domain.entity.person.Person;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;

public class TestEntityFactory {

    public static UserTypeEntity createUserTypeAdmin() {
        UserTypeEntity userTypeEntity = new UserTypeEntity();
        userTypeEntity.setId(1);
        userTypeEntity.setUserTypeName(UserTypeEnum.ADMIN.name());

        return userTypeEntity;
    }

    public static UserTypeEntity createUserTypeProductManager() {
        UserTypeEntity userTypeEntity = new UserTypeEntity();
        userTypeEntity.setId(2);
        userTypeEntity.setUserTypeName(UserTypeEnum.PRODUCT_MANAGER.name());

        return userTypeEntity;
    }

    public static UserTypeEntity createUserTypeCommon() {
        UserTypeEntity userTypeEntity = new UserTypeEntity();
        userTypeEntity.setId(3);
        userTypeEntity.setUserTypeName(UserTypeEnum.COMMON.name());

        return userTypeEntity;
    }

    public static Person createPerson() {
        Person person = new Person();
        person.setEmail("teste@exemplo.com");
        person.setPersonName("Usuário teste");

        return person;
    }

    public static User createUser() {
        Person person = new Person();
        person.setEmail("teste@exemplo.com");
        person.setPersonName("Usuário Teste");

        User user = new User();
        user.setPerson(person);
        return user;
    }

    public static CategoryEntity createCategory() {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(1);
        categoryEntity.setCategoryName(CategoryEnum.OTHERS.name());
        return categoryEntity;
    }

    public static Product createProduct() {
        CategoryEntity categoryEntity = TestEntityFactory.createCategory();

        Product product = new Product();
        product.setId(1L);
        product.setProductName("Produto Teste");
        product.setPrice(BigDecimal.valueOf(100));
        product.setCategory(categoryEntity);

        return product;
    }

    public static Movement createMovement() {

        Product product = createProduct();
        product.setStock(100);

        Movement movement = new Movement();
        movement.setId(1L);
        movement.setProduct(product);
        movement.setQuantity(10);
        movement.setUnitPrice(new BigDecimal("5.50"));
        movement.setTotalValue(new BigDecimal("55.00"));
        movement.setMovementationDate(LocalDateTime.now());
        movement.setUser(createUser());
        movement.setOrder(null);

        return movement;
    }

}
