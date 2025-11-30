package com.github.renancvitor.inventory.controller.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
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

import com.github.renancvitor.inventory.application.order.controller.OrderController;
import com.github.renancvitor.inventory.application.order.dto.OrderCreationData;
import com.github.renancvitor.inventory.application.order.dto.OrderDetailData;
import com.github.renancvitor.inventory.application.order.dto.OrderItemRequest;
import com.github.renancvitor.inventory.application.order.repository.OrderItemRepository;
import com.github.renancvitor.inventory.application.order.repository.OrderRepository;
import com.github.renancvitor.inventory.application.order.repository.OrderStatusRepository;
import com.github.renancvitor.inventory.application.order.service.OrderService;
import com.github.renancvitor.inventory.domain.entity.movement.MovementTypeEntity;
import com.github.renancvitor.inventory.domain.entity.order.Order;
import com.github.renancvitor.inventory.domain.entity.order.OrderItem;
import com.github.renancvitor.inventory.domain.entity.order.OrderStatusEntity;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderControllerCreationTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @Mock
    private SystemLogPublisherService logPublisherService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderItem orderItem;
    private Order order;
    private User loggedInUser;
    private Product product;
    private OrderStatusEntity orderStatusEntity;
    private MovementTypeEntity movementTypeEntityOutput;
    private MovementTypeEntity movementTypeEntityInput;

    @BeforeEach
    void setup() {
        orderItem = TestEntityFactory.createOrderItem();
        order = TestEntityFactory.createOrder();
        loggedInUser = TestEntityFactory.createUser();
        product = TestEntityFactory.createProduct();
        orderStatusEntity = TestEntityFactory.createStatusPending();
        movementTypeEntityOutput = TestEntityFactory.createMovementTypeOutput();
        movementTypeEntityInput = TestEntityFactory.createMovementTypeInput();
    }

    @Nested
    class PositiveCases {
        @SuppressWarnings("null")
        @Test
        void shouldCreateOrderSuccessfully() {
            OrderCreationData creationData = new OrderCreationData(
                    "Description",
                    List.of(
                            new OrderItemRequest(
                                    product.getId(),
                                    movementTypeEntityInput.getId(),
                                    5,
                                    BigDecimal.valueOf(20.00))));

            OrderDetailData detailData = new OrderDetailData(order);

            when(orderService.create(eq(creationData), eq(loggedInUser)))
                    .thenReturn(detailData);

            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

            ResponseEntity<OrderDetailData> response = orderController.create(
                    creationData,
                    uriComponentsBuilder,
                    loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getHeaders().getLocation())
                    .isEqualTo(URI.create("http://localhost/orders/" + 1L));
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().id()).isEqualTo(1L);
            assertThat(response.getBody().description()).isEqualTo("Description");

            verify(orderService).create(eq(creationData), eq(loggedInUser));
        }
    }

    @Nested
    class NegativeCases {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        @Test
        void shouldFailValidationWhenDescriptionIsBlank() {
            OrderCreationData data = new OrderCreationData(
                    "",
                    List.of(
                            new OrderItemRequest(
                                    product.getId(),
                                    movementTypeEntityInput.getId(),
                                    5,
                                    BigDecimal.valueOf(10))));

            Set<ConstraintViolation<OrderCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("description"));
        }

        @Test
        void shouldFailValidationWhenItemsIsNull() {
            OrderCreationData data = new OrderCreationData(
                    "Pedido inválido",
                    null);

            Set<ConstraintViolation<OrderCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("items"));
        }

        @Test
        void shouldFailValidationWhenItemsIsEmpty() {
            OrderCreationData data = new OrderCreationData(
                    "Pedido inválido",
                    List.of());

            Set<ConstraintViolation<OrderCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("items"));
        }

        @Test
        void shouldFailValidationWhenProductIdIsNull() {
            OrderCreationData data = new OrderCreationData(
                    "Pedido inválido",
                    List.of(
                            new OrderItemRequest(
                                    null,
                                    movementTypeEntityInput.getId(),
                                    5,
                                    BigDecimal.valueOf(10))));

            Set<ConstraintViolation<OrderCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("items[0].productId"));
        }

        @Test
        void shouldFailValidationWhenMovementTypeIdIsNull() {
            OrderCreationData data = new OrderCreationData(
                    "Pedido inválido",
                    List.of(
                            new OrderItemRequest(
                                    product.getId(),
                                    null,
                                    5,
                                    BigDecimal.valueOf(10))));

            Set<ConstraintViolation<OrderCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("items[0].movementTypeId"));
        }

        @Test
        void shouldFailValidationWhenQuantityIsNull() {
            OrderCreationData data = new OrderCreationData(
                    "Pedido inválido",
                    List.of(
                            new OrderItemRequest(
                                    product.getId(),
                                    movementTypeEntityInput.getId(),
                                    null,
                                    BigDecimal.valueOf(10))));

            Set<ConstraintViolation<OrderCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("items[0].quantity"));
        }

        @Test
        void shouldFailValidationWhenQuantityIsNegativeOrZero() {
            OrderCreationData data = new OrderCreationData(
                    "Pedido inválido",
                    List.of(
                            new OrderItemRequest(
                                    product.getId(),
                                    movementTypeEntityInput.getId(),
                                    -10,
                                    BigDecimal.valueOf(10))));

            Set<ConstraintViolation<OrderCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("items[0].quantity"));
        }

        @Test
        void shouldFailValidationWhenUnitPriceIsNull() {
            OrderCreationData data = new OrderCreationData(
                    "Pedido inválido",
                    List.of(
                            new OrderItemRequest(
                                    product.getId(),
                                    movementTypeEntityInput.getId(),
                                    5,
                                    null)));

            Set<ConstraintViolation<OrderCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("items[0].unitPrice"));
        }

        @Test
        void shouldFailValidationWhenUnitPriceIsNegativeOrZero() {
            OrderCreationData data = new OrderCreationData(
                    "Pedido inválido",
                    List.of(
                            new OrderItemRequest(
                                    product.getId(),
                                    movementTypeEntityInput.getId(),
                                    5,
                                    BigDecimal.valueOf(-5))));

            Set<ConstraintViolation<OrderCreationData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("items[0].unitPrice"));
        }

        @Test
        void shouldPropagateExceptionWhenServiceFails() {
            OrderCreationData creationData = new OrderCreationData(
                    "Pedido com problema",
                    List.of(
                            new OrderItemRequest(
                                    product.getId(),
                                    movementTypeEntityOutput.getId(),
                                    10,
                                    BigDecimal.valueOf(75.50))));

            RuntimeException exception = new RuntimeException("Failed to create order");

            when(orderService.create(any(), any()))
                    .thenThrow(exception);

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/orders");

            assertThatThrownBy(() -> orderController.create(
                    creationData,
                    uriBuilder,
                    loggedInUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to create order");

            verify(orderService).create(any(), eq(loggedInUser));
        }
    }

}
