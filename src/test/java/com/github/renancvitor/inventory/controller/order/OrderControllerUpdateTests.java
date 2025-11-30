package com.github.renancvitor.inventory.controller.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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

import com.github.renancvitor.inventory.application.movement.dto.MovementOrderRequest;
import com.github.renancvitor.inventory.application.order.controller.OrderController;
import com.github.renancvitor.inventory.application.order.dto.OrderDetailData;
import com.github.renancvitor.inventory.application.order.dto.OrderUpdateData;
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
public class OrderControllerUpdateTests {

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
        @Test
        void shouldUpdateOrderSuccessfully() {
            Long id = 1L;

            OrderUpdateData updateData = new OrderUpdateData(
                    "Pedido atualizado",
                    List.of(
                            new MovementOrderRequest(
                                    product.getId(),
                                    movementTypeEntityInput.getId(),
                                    5,
                                    BigDecimal.valueOf(20.00))));

            OrderDetailData returnData = new OrderDetailData(order);

            when(orderService.update(eq(id), eq(updateData), eq(loggedInUser)))
                    .thenReturn(returnData);

            ResponseEntity<OrderDetailData> response = orderController.update(
                    id,
                    updateData,
                    loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(returnData);

            verify(orderService).update(id, updateData, loggedInUser);
        }
    }

    @Nested
    class NegativeCases {
        private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        @Test
        void shouldFailValidationWhenMovementsIsNull() {
            OrderUpdateData data = new OrderUpdateData(
                    "Descrição válida",
                    null);

            Set<ConstraintViolation<OrderUpdateData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("movements"));
        }

        @Test
        void shouldFailValidationWhenMovementProductIdIsNull() {
            OrderUpdateData data = new OrderUpdateData(
                    "Descrição válida",
                    List.of(
                            new MovementOrderRequest(
                                    null,
                                    movementTypeEntityInput.getId(),
                                    5,
                                    BigDecimal.valueOf(10))));

            Set<ConstraintViolation<OrderUpdateData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("movements[0].productId"));
        }

        @Test
        void shouldFailValidationWhenMovementTypeIdIsNull() {
            OrderUpdateData data = new OrderUpdateData(
                    "Descrição válida",
                    List.of(
                            new MovementOrderRequest(
                                    product.getId(),
                                    null,
                                    5,
                                    BigDecimal.valueOf(10))));

            Set<ConstraintViolation<OrderUpdateData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("movements[0].movementTypeId"));
        }

        @Test
        void shouldFailValidationWhenQuantityIsNull() {
            OrderUpdateData data = new OrderUpdateData(
                    "Descrição válida",
                    List.of(
                            new MovementOrderRequest(
                                    product.getId(),
                                    movementTypeEntityInput.getId(),
                                    null,
                                    BigDecimal.valueOf(10))));

            Set<ConstraintViolation<OrderUpdateData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("movements[0].quantity"));
        }

        @Test
        void shouldFailValidationWhenQuantityIsZeroOrNegative() {
            OrderUpdateData data = new OrderUpdateData(
                    "Descrição válida",
                    List.of(
                            new MovementOrderRequest(
                                    product.getId(),
                                    movementTypeEntityInput.getId(),
                                    -1,
                                    BigDecimal.valueOf(10))));

            Set<ConstraintViolation<OrderUpdateData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("movements[0].quantity"));
        }

        @Test
        void shouldFailValidationWhenUnitPriceIsNull() {
            OrderUpdateData data = new OrderUpdateData(
                    "Descrição válida",
                    List.of(
                            new MovementOrderRequest(
                                    product.getId(),
                                    movementTypeEntityInput.getId(),
                                    5,
                                    null)));

            Set<ConstraintViolation<OrderUpdateData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("movements[0].unitPrice"));
        }

        @Test
        void shouldFailValidationWhenUnitPriceIsZeroOrNegative() {
            OrderUpdateData data = new OrderUpdateData(
                    "Descrição válida",
                    List.of(
                            new MovementOrderRequest(
                                    product.getId(),
                                    movementTypeEntityInput.getId(),
                                    5,
                                    BigDecimal.valueOf(0))));

            Set<ConstraintViolation<OrderUpdateData>> violations = validator.validate(data);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                    v -> v.getPropertyPath()
                            .toString()
                            .equals("movements[0].unitPrice"));
        }

        @Test
        void shouldPropagateExceptionWhenServiceFails() {
            Long id = 1L;

            OrderUpdateData updateData = new OrderUpdateData(
                    "Pedido com problema",
                    List.of(
                            new MovementOrderRequest(
                                    product.getId(),
                                    movementTypeEntityOutput.getId(),
                                    10,
                                    BigDecimal.valueOf(50))));

            RuntimeException exception = new RuntimeException("Erro ao atualizar pedido");

            when(orderService.update(any(), any(), any()))
                    .thenThrow(exception);

            assertThatThrownBy(() -> orderController.update(id, updateData, loggedInUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Erro ao atualizar pedido");

            verify(orderService).update(any(), any(), eq(loggedInUser));
        }
    }

}
