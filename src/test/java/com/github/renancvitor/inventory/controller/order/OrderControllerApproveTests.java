package com.github.renancvitor.inventory.controller.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import com.github.renancvitor.inventory.domain.entity.user.exception.AccessDeniedException;
import com.github.renancvitor.inventory.exception.types.common.EntityNotFoundException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderControllerApproveTests {

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
        void shouldApproveOrderSuccessfully() {
            OrderDetailData detailData = new OrderDetailData(order);

            when(orderService.approve(eq(order.getId()), eq(loggedInUser)))
                    .thenReturn(detailData);

            ResponseEntity<OrderDetailData> response = orderController.approve(order.getId(), loggedInUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().id()).isEqualTo(order.getId());

            verify(orderService).approve(eq(order.getId()), eq(loggedInUser));
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldReturn404WhenOrderNotFound() {
            Long nonExistentId = 999L;

            when(orderService.approve(eq(nonExistentId), eq(loggedInUser)))
                    .thenThrow(new EntityNotFoundException("Pedido não encontrado", nonExistentId));

            assertThatThrownBy(() -> orderController.approve(nonExistentId, loggedInUser))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Pedido não encontrado");
        }

        @Test
        void shouldFailWhenUserDoesNotHavePermissionToApprove() {
            when(orderService.approve(eq(order.getId()), eq(loggedInUser)))
                    .thenThrow(new AccessDeniedException("Usuário não autorizado a aprovar pedidos"));

            assertThatThrownBy(() -> orderController.approve(order.getId(), loggedInUser))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("não autorizado");
        }

        @Test
        void shouldPropagateUnexpectedException() {
            when(orderService.approve(eq(order.getId()), eq(loggedInUser)))
                    .thenThrow(new RuntimeException("Erro inesperado"));

            assertThatThrownBy(() -> orderController.approve(order.getId(), loggedInUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Erro inesperado");
        }
    }

}
