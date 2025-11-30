package com.github.renancvitor.inventory.controller.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.github.renancvitor.inventory.application.order.controller.OrderController;
import com.github.renancvitor.inventory.application.order.dto.OrderDetailData;
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

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderControllerRejectTests {

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
        void shouldRejectOrderSuccessfully() {
            OrderDetailData detailData = new OrderDetailData(order);

            when(orderService.reject(eq(order.getId()), eq(loggedInUser)))
                    .thenReturn(detailData);

            ResponseEntity<OrderDetailData> response = orderController.reject(
                    order.getId(),
                    loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(detailData);

            verify(orderService).reject(order.getId(), loggedInUser);
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldPropagateExceptionWhenServiceFails() {
            RuntimeException exception = new RuntimeException("Erro ao rejeitar pedido");

            when(orderService.reject(any(), any()))
                    .thenThrow(exception);

            assertThatThrownBy(() -> orderController.reject(order.getId(), loggedInUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Erro ao rejeitar pedido");

            verify(orderService).reject(any(), eq(loggedInUser));
        }
    }

}
