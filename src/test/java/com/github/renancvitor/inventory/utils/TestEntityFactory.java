package com.github.renancvitor.inventory.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.github.renancvitor.inventory.domain.entity.category.CategoryEntity;
import com.github.renancvitor.inventory.domain.entity.category.enums.CategoryEnum;
import com.github.renancvitor.inventory.domain.entity.movement.Movement;
import com.github.renancvitor.inventory.domain.entity.movement.MovementTypeEntity;
import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;
import com.github.renancvitor.inventory.domain.entity.order.Order;
import com.github.renancvitor.inventory.domain.entity.order.OrderItem;
import com.github.renancvitor.inventory.domain.entity.order.OrderStatusEntity;
import com.github.renancvitor.inventory.domain.entity.order.enums.OrderStatusEnum;
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

    public static MovementTypeEntity createMovementTypeInput() {
        MovementTypeEntity type = new MovementTypeEntity();
        type.setId(1);
        type.setMovementTypeName(MovementTypeEnum.INPUT.name());

        return type;
    }

    public static MovementTypeEntity createMovementTypeOutput() {
        MovementTypeEntity type = new MovementTypeEntity();
        type.setId(2);
        type.setMovementTypeName(MovementTypeEnum.OUTPUT.name());

        return type;
    }

    public static Movement createMovement() {
        Product product = createProduct();
        product.setStock(100);

        Movement movement = new Movement();
        movement.setId(1L);
        movement.setProduct(product);
        movement.setMovementType(createMovementTypeOutput());
        movement.setQuantity(10);
        movement.setUnitPrice(new BigDecimal("5.50"));
        movement.setTotalValue(new BigDecimal("55.00"));
        movement.setMovementationDate(LocalDateTime.now());
        movement.setUser(createUser());
        movement.setOrder(null);

        return movement;
    }

    public static OrderStatusEntity createStatusPending() {
        OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
        orderStatusEntity.setId(1);
        orderStatusEntity.setOrderStatusName(OrderStatusEnum.PENDING.name());

        return orderStatusEntity;
    }

    public static OrderStatusEntity createStatusApproved() {
        OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
        orderStatusEntity.setId(2);
        orderStatusEntity.setOrderStatusName(OrderStatusEnum.APPROVED.name());

        return orderStatusEntity;
    }

    public static OrderItem createOrderItem() {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProduct(createProduct());
        orderItem.setMovementType(MovementTypeEntity.fromEnum(MovementTypeEnum.INPUT));
        orderItem.setQuantity(12);
        orderItem.setUnitPrice(BigDecimal.valueOf(10));
        orderItem.setTotalValue(BigDecimal.valueOf(120));

        return orderItem;
    }

    public static Order createOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderStatus(createStatusPending());
        order.setRejectedBy(createUser());
        order.setMovements(List.of(createMovement()));
        order.setTotalValue(BigDecimal.valueOf(120));
        order.setDescription("Description");
        order.setItems(new ArrayList<>());

        return order;
    }

}
