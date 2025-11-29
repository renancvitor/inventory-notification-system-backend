package com.github.renancvitor.inventory.service.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.movement.repository.MovementTypeRepository;
import com.github.renancvitor.inventory.application.order.dto.OrderCreationData;
import com.github.renancvitor.inventory.application.order.dto.OrderDetailData;
import com.github.renancvitor.inventory.application.order.dto.OrderItemRequest;
import com.github.renancvitor.inventory.application.order.dto.OrderLogData;
import com.github.renancvitor.inventory.application.order.repository.OrderItemRepository;
import com.github.renancvitor.inventory.application.order.repository.OrderRepository;
import com.github.renancvitor.inventory.application.order.repository.OrderStatusRepository;
import com.github.renancvitor.inventory.application.order.service.OrderService;
import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.domain.entity.movement.MovementTypeEntity;
import com.github.renancvitor.inventory.domain.entity.order.Order;
import com.github.renancvitor.inventory.domain.entity.order.OrderItem;
import com.github.renancvitor.inventory.domain.entity.order.OrderStatusEntity;
import com.github.renancvitor.inventory.domain.entity.order.enums.OrderStatusEnum;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderServiceCreateTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MovementTypeRepository movementTypeRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @Mock
    private SystemLogPublisherService logPublisherService;

    @InjectMocks
    private OrderService orderService;

    private OrderItem orderItem;
    private Order order;
    private User loggedInUser;
    private Product product;
    private OrderStatusEntity orderStatusEntity;
    private MovementTypeEntity movementTypeEntity;

    @BeforeEach
    void setup() {
        orderItem = TestEntityFactory.createOrderItem();
        order = TestEntityFactory.createOrder();
        loggedInUser = TestEntityFactory.createUser();
        product = TestEntityFactory.createProduct();
        orderStatusEntity = TestEntityFactory.createStatusPending();
        movementTypeEntity = TestEntityFactory.createMovementTypeInput();
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldCreateOrderSuccessfully() {
            OrderItemRequest itemDto = new OrderItemRequest(
                    1L,
                    1,
                    5,
                    BigDecimal.valueOf(10));

            OrderCreationData data = new OrderCreationData(
                    "Description",
                    List.of(itemDto));

            when(orderStatusRepository.findById(OrderStatusEnum.PENDING.getId()))
                    .thenReturn(Optional.of(orderStatusEntity));

            when(productRepository.findById(1L))
                    .thenReturn(Optional.of(product));

            when(movementTypeRepository.findById(1))
                    .thenReturn(Optional.of(movementTypeEntity));

            when(orderRepository.save(any(Order.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            OrderDetailData result = orderService.create(data, loggedInUser);

            assertNotNull(result);
            assertEquals("Description", result.description());

            verify(orderStatusRepository).findById(OrderStatusEnum.PENDING.getId());
            verify(productRepository).findById(1L);
            verify(movementTypeRepository).findById(1);
            verify(orderRepository).save(any(Order.class));
            verify(logPublisherService)
                    .publish(eq("ORDER_CREATED"),
                            contains(loggedInUser.getUsername()),
                            isNull(),
                            any(OrderLogData.class));
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldThrowWhenPendingStatusNotFound() {
            OrderCreationData data = new OrderCreationData(
                    "Description",
                    List.of());

            when(orderStatusRepository.findById(OrderStatusEnum.PENDING.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> orderService.create(data, loggedInUser));

            verify(orderStatusRepository)
                    .findById(OrderStatusEnum.PENDING.getId());
        }

        @Test
        void shouldThrowWhenProductNotFound() {
            OrderItemRequest item = new OrderItemRequest(
                    99L, 1, 5, BigDecimal.valueOf(10));

            OrderCreationData data = new OrderCreationData(
                    "Description", List.of(item));

            when(orderStatusRepository.findById(OrderStatusEnum.PENDING.getId()))
                    .thenReturn(Optional.of(orderStatusEntity));

            when(productRepository.findById(99L))
                    .thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class,
                    () -> orderService.create(data, loggedInUser));

            verify(productRepository).findById(99L);
        }

        @Test
        void shouldThrowWhenMovementTypeNotFound() {
            OrderItemRequest item = new OrderItemRequest(
                    1L, 999, 5, BigDecimal.valueOf(10));

            OrderCreationData data = new OrderCreationData(
                    "Description", List.of(item));

            when(orderStatusRepository.findById(OrderStatusEnum.PENDING.getId()))
                    .thenReturn(Optional.of(orderStatusEntity));

            when(productRepository.findById(1L))
                    .thenReturn(Optional.of(product));

            when(movementTypeRepository.findById(999))
                    .thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class,
                    () -> orderService.create(data, loggedInUser));

            verify(movementTypeRepository).findById(999);
        }

        @Test
        void shouldThrowWhenRepositorySaveFails() {
            OrderItemRequest item = new OrderItemRequest(
                    1L, 1, 5, BigDecimal.valueOf(10));

            OrderCreationData data = new OrderCreationData(
                    "Description", List.of(item));

            when(orderStatusRepository.findById(OrderStatusEnum.PENDING.getId()))
                    .thenReturn(Optional.of(orderStatusEntity));

            when(productRepository.findById(1L))
                    .thenReturn(Optional.of(product));

            when(movementTypeRepository.findById(1))
                    .thenReturn(Optional.of(movementTypeEntity));

            when(orderRepository.save(any(Order.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class,
                    () -> orderService.create(data, loggedInUser));

            verify(orderRepository).save(any(Order.class));
        }

        @Test
        void shouldThrowWhenLogPublisherFails() {
            OrderItemRequest item = new OrderItemRequest(
                    1L, 1, 5, BigDecimal.valueOf(10));

            OrderCreationData data = new OrderCreationData(
                    "Description", List.of(item));

            when(orderStatusRepository.findById(OrderStatusEnum.PENDING.getId()))
                    .thenReturn(Optional.of(orderStatusEntity));

            when(productRepository.findById(1L))
                    .thenReturn(Optional.of(product));

            when(movementTypeRepository.findById(1))
                    .thenReturn(Optional.of(movementTypeEntity));

            when(orderRepository.save(any(Order.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            doThrow(new RuntimeException("log error"))
                    .when(logPublisherService)
                    .publish(any(), any(), any(), any());

            assertThrows(RuntimeException.class,
                    () -> orderService.create(data, loggedInUser));

            verify(logPublisherService).publish(any(), any(), any(), any());
        }

        @Test
        void shouldThrowWhenItemsListIsNull() {
            OrderCreationData data = new OrderCreationData(
                    "Description",
                    null);

            when(orderStatusRepository.findById(OrderStatusEnum.PENDING.getId()))
                    .thenReturn(Optional.of(orderStatusEntity));

            assertThrows(NullPointerException.class,
                    () -> orderService.create(data, loggedInUser));

            verify(orderStatusRepository)
                    .findById(OrderStatusEnum.PENDING.getId());
        }

        @Test
        void shouldThrowWhenLoggedUserIsNull() {
            OrderCreationData data = new OrderCreationData(
                    "Description",
                    List.of());

            when(orderStatusRepository.findById(OrderStatusEnum.PENDING.getId()))
                    .thenReturn(Optional.of(orderStatusEntity));

            assertThrows(NullPointerException.class,
                    () -> orderService.create(data, null));

            verify(orderStatusRepository)
                    .findById(OrderStatusEnum.PENDING.getId());
        }
    }

}
