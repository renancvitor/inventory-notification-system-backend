package com.github.renancvitor.inventory.service.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
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

import com.github.renancvitor.inventory.application.movement.dto.MovementOrderRequest;
import com.github.renancvitor.inventory.application.movement.repository.MovementTypeRepository;
import com.github.renancvitor.inventory.application.order.dto.OrderDetailData;
import com.github.renancvitor.inventory.application.order.dto.OrderLogData;
import com.github.renancvitor.inventory.application.order.dto.OrderUpdateData;
import com.github.renancvitor.inventory.application.order.repository.OrderItemRepository;
import com.github.renancvitor.inventory.application.order.repository.OrderRepository;
import com.github.renancvitor.inventory.application.order.repository.OrderStatusRepository;
import com.github.renancvitor.inventory.application.order.service.OrderService;
import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.domain.entity.movement.MovementTypeEntity;
import com.github.renancvitor.inventory.domain.entity.order.Order;
import com.github.renancvitor.inventory.domain.entity.order.OrderItem;
import com.github.renancvitor.inventory.domain.entity.order.OrderStatusEntity;
import com.github.renancvitor.inventory.domain.entity.order.exceptions.OrderStatusException;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.exception.AccessDeniedException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderServiceUpdateTests {

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
                void shouldUpdateOrderSuccessfully() {
                        MovementOrderRequest movement = new MovementOrderRequest(
                                        1L,
                                        1,
                                        5,
                                        BigDecimal.valueOf(10));

                        OrderUpdateData data = new OrderUpdateData(
                                        "Updated description",
                                        List.of(movement));

                        order.setRequestedBy(loggedInUser);
                        order.setOrderStatus(orderStatusEntity);
                        order.setDescription(data.description());

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        when(productRepository.findByIdAndActiveTrue(product.getId()))
                                        .thenReturn(Optional.of(product));

                        when(movementTypeRepository.findById(movementTypeEntity.getId()))
                                        .thenReturn(Optional.of(movementTypeEntity));

                        when(orderRepository.save(any(Order.class)))
                                        .thenAnswer(inv -> inv.getArgument(0));

                        OrderDetailData result = orderService.update(order.getId(), data, loggedInUser);

                        assertNotNull(result);
                        assertEquals("Updated description", result.description());
                        assertEquals(1, result.movements().size());

                        verify(orderRepository).findById(order.getId());
                        verify(productRepository).findByIdAndActiveTrue(product.getId());
                        verify(movementTypeRepository).findById(movementTypeEntity.getId());
                        verify(orderRepository).save(any(Order.class));
                        verify(logPublisherService)
                                        .publish(eq("ORDER_UPDATED"),
                                                        contains(loggedInUser.getUsername()),
                                                        any(OrderLogData.class),
                                                        any(OrderLogData.class));
                }
        }

        @Nested
        class NegativeCases {
                @Test
                void shouldThrowWhenOrderNotFound() {
                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.empty());

                        OrderUpdateData data = new OrderUpdateData(
                                        "desc",
                                        List.of());

                        assertThrows(RuntimeException.class,
                                        () -> orderService.update(order.getId(), data, loggedInUser));

                        verify(orderRepository).findById(order.getId());
                }

                @Test
                void shouldThrowWhenLoggedUserIsNotOwner() {
                        User ownerUser = TestEntityFactory.createUser();
                        ownerUser.setId(2L);

                        order.setRequestedBy(ownerUser);

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        OrderUpdateData data = new OrderUpdateData(
                                        "desc",
                                        List.of());

                        assertThrows(AccessDeniedException.class,
                                        () -> orderService.update(
                                                        order.getId(),
                                                        data,
                                                        loggedInUser));

                        verify(orderRepository).findById(order.getId());
                }

                @Test
                void shouldThrowWhenStatusIsNotPending() {
                        order.setRequestedBy(loggedInUser);

                        orderStatusEntity = TestEntityFactory.createStatusApproved();
                        order.setOrderStatus(orderStatusEntity);

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        OrderUpdateData data = new OrderUpdateData(
                                        "desc",
                                        List.of());

                        assertThrows(OrderStatusException.class,
                                        () -> orderService.update(order.getId(), data, loggedInUser));

                        verify(orderRepository).findById(order.getId());
                }

                @Test
                void shouldThrowWhenMovementsIsNull() {
                        OrderUpdateData data = new OrderUpdateData(
                                        "desc",
                                        null);

                        order.setRequestedBy(loggedInUser);
                        order.setOrderStatus(orderStatusEntity);

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        assertThrows(NullPointerException.class,
                                        () -> orderService.update(order.getId(), data, loggedInUser));

                        verify(orderRepository).findById(order.getId());
                }

                @Test
                void shouldThrowWhenProductNotFound() {
                        MovementOrderRequest movement = new MovementOrderRequest(
                                        product.getId(),
                                        movementTypeEntity.getId(),
                                        5,
                                        BigDecimal.valueOf(10));

                        OrderUpdateData data = new OrderUpdateData(
                                        "desc",
                                        List.of(movement));

                        order.setRequestedBy(loggedInUser);
                        order.setOrderStatus(orderStatusEntity);

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        when(productRepository.findByIdAndActiveTrue(product.getId()))
                                        .thenReturn(Optional.empty());

                        assertThrows(RuntimeException.class,
                                        () -> orderService.update(order.getId(), data, loggedInUser));

                        verify(productRepository).findByIdAndActiveTrue(product.getId());
                }

                @Test
                void shouldThrowWhenMovementTypeNotFound() {
                        MovementOrderRequest movement = new MovementOrderRequest(
                                        product.getId(),
                                        movementTypeEntity.getId(),
                                        5,
                                        BigDecimal.valueOf(10));

                        OrderUpdateData data = new OrderUpdateData(
                                        "desc",
                                        List.of(movement));

                        order.setRequestedBy(loggedInUser);
                        order.setOrderStatus(orderStatusEntity);

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        when(productRepository.findByIdAndActiveTrue(product.getId()))
                                        .thenReturn(Optional.of(product));

                        when(movementTypeRepository.findById(movementTypeEntity.getId()))
                                        .thenReturn(Optional.empty());

                        assertThrows(RuntimeException.class,
                                        () -> orderService.update(order.getId(), data, loggedInUser));

                        verify(movementTypeRepository).findById(movementTypeEntity.getId());
                }

                @Test
                void shouldThrowWhenLoggedUserIsNull() {
                        MovementOrderRequest movement = new MovementOrderRequest(
                                        product.getId(),
                                        movementTypeEntity.getId(),
                                        5,
                                        BigDecimal.valueOf(10));

                        OrderUpdateData data = new OrderUpdateData(
                                        "desc",
                                        List.of(movement));

                        order.setRequestedBy(loggedInUser);
                        order.setOrderStatus(orderStatusEntity);

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        assertThrows(AccessDeniedException.class,
                                        () -> orderService.update(order.getId(), data, null));

                        verify(orderRepository).findById(order.getId());
                }

                @Test
                void shouldThrowWhenRepositoryFailsDuringSave() {
                        MovementOrderRequest movement = new MovementOrderRequest(
                                        product.getId(),
                                        movementTypeEntity.getId(),
                                        5,
                                        BigDecimal.valueOf(10));

                        OrderUpdateData data = new OrderUpdateData(
                                        "desc",
                                        List.of(movement));

                        order.setRequestedBy(loggedInUser);
                        order.setOrderStatus(orderStatusEntity);

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        when(productRepository.findByIdAndActiveTrue(product.getId()))
                                        .thenReturn(Optional.of(product));

                        when(movementTypeRepository.findById(movementTypeEntity.getId()))
                                        .thenReturn(Optional.of(movementTypeEntity));

                        when(orderRepository.save(any(Order.class)))
                                        .thenThrow(new RuntimeException("DB error"));

                        assertThrows(RuntimeException.class,
                                        () -> orderService.update(order.getId(), data, loggedInUser));

                        verify(orderRepository).save(any(Order.class));
                }
        }

}
