package com.github.renancvitor.inventory.service.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.movement.repository.MovementTypeRepository;
import com.github.renancvitor.inventory.application.movement.service.MovementService;
import com.github.renancvitor.inventory.application.order.dto.OrderDetailData;
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
import com.github.renancvitor.inventory.domain.entity.order.exceptions.OrderStatusException;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderServiceApproveTests {

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

        @Mock
        private AuthenticationService authenticationService;

        @Mock
        private MovementService movementService;

        @InjectMocks
        private OrderService orderService;

        private OrderItem orderItem;
        private Order order;
        private User loggedInUser;
        private OrderStatusEntity orderStatusEntity;
        private OrderStatusEntity orderStatusEntityApproved;
        private MovementTypeEntity movementTypeEntity;

        @BeforeEach
        void setup() {
                orderItem = TestEntityFactory.createOrderItem();
                order = TestEntityFactory.createOrder();
                loggedInUser = TestEntityFactory.createUser();
                orderStatusEntity = TestEntityFactory.createStatusPending();
                movementTypeEntity = TestEntityFactory.createMovementTypeInput();
                orderStatusEntityApproved = TestEntityFactory.createStatusApproved();
        }

        @Nested
        class PositiveCases {
                @Test
                void shouldApproveOrderSuccessfully() {
                        order.setOrderStatus(orderStatusEntity);
                        order.setRequestedBy(loggedInUser);

                        orderItem.setOrder(order);
                        order.setItems(List.of(orderItem));

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        when(orderStatusRepository.findById(OrderStatusEnum.APPROVED.getId()))
                                        .thenReturn(Optional.of(orderStatusEntityApproved));

                        when(orderRepository.save(any(Order.class)))
                                        .thenAnswer(inv -> inv.getArgument(0));

                        OrderDetailData result = orderService.approve(order.getId(), loggedInUser);

                        assertNotNull(result);
                        assertEquals(OrderStatusEnum.APPROVED.getDisplayName(), result.status());

                        verify(orderRepository).findById(order.getId());
                        verify(orderStatusRepository).findById(OrderStatusEnum.APPROVED.getId());
                        verify(orderRepository).save(any(Order.class));

                        verify(movementService).input(any(), eq(loggedInUser), eq(order));

                        verify(logPublisherService).publish(
                                        eq("ORDER_APPROVED"),
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

                        assertThrows(RuntimeException.class,
                                        () -> orderService.approve(order.getId(), loggedInUser));

                        verify(orderRepository).findById(order.getId());
                }

                @Test
                void shouldThrowWhenStatusIsNotPending() {
                        order.setOrderStatus(orderStatusEntityApproved);

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        assertThrows(OrderStatusException.class,
                                        () -> orderService.approve(order.getId(), loggedInUser));

                        verify(orderRepository).findById(order.getId());
                }

                @Test
                void shouldThrowWhenApprovedStatusNotFound() {
                        order.setOrderStatus(orderStatusEntity);

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        when(orderStatusRepository.findById(OrderStatusEnum.APPROVED.getId()))
                                        .thenReturn(Optional.empty());

                        assertThrows(OrderStatusException.class,
                                        () -> orderService.approve(order.getId(), loggedInUser));

                        verify(orderStatusRepository).findById(OrderStatusEnum.APPROVED.getId());
                }

                @Test
                void shouldThrowWhenLoggedUserIsNull() {
                        order.setOrderStatus(orderStatusEntity);

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        when(orderStatusRepository.findById(OrderStatusEnum.APPROVED.getId()))
                                        .thenReturn(Optional.of(orderStatusEntityApproved));

                        assertThrows(NullPointerException.class,
                                        () -> orderService.approve(order.getId(), null));
                }

                @Test
                void shouldThrowWhenMovementServiceInputFails() {
                        order.setOrderStatus(orderStatusEntity);

                        orderItem.setMovementType(movementTypeEntity);
                        orderItem.setOrder(order);
                        order.setItems(List.of(orderItem));

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        when(orderStatusRepository.findById(OrderStatusEnum.APPROVED.getId()))
                                        .thenReturn(Optional.of(orderStatusEntityApproved));

                        when(orderRepository.save(any(Order.class)))
                                        .thenAnswer(inv -> inv.getArgument(0));

                        doThrow(new RuntimeException("input error"))
                                        .when(movementService)
                                        .input(any(), eq(loggedInUser), eq(order));

                        assertThrows(RuntimeException.class,
                                        () -> orderService.approve(order.getId(), loggedInUser));
                }

                @Test
                void shouldThrowWhenMovementServiceOutputFails() {
                        order.setOrderStatus(orderStatusEntity);

                        MovementTypeEntity movementTypeEntityOutput = TestEntityFactory.createMovementTypeOutput();
                        orderItem.setMovementType(movementTypeEntityOutput);
                        orderItem.setOrder(order);
                        order.setItems(List.of(orderItem));

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        when(orderStatusRepository.findById(OrderStatusEnum.APPROVED.getId()))
                                        .thenReturn(Optional.of(orderStatusEntityApproved));

                        when(orderRepository.save(any(Order.class)))
                                        .thenAnswer(inv -> inv.getArgument(0));

                        doThrow(new RuntimeException("output error"))
                                        .when(movementService)
                                        .output(any(), eq(loggedInUser), eq(order));

                        assertThrows(RuntimeException.class,
                                        () -> orderService.approve(order.getId(), loggedInUser));
                }

                @Test
                void shouldThrowWhenRepositorySaveFails() {
                        order.setOrderStatus(orderStatusEntity);
                        order.setItems(List.of(orderItem));

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        when(orderStatusRepository.findById(OrderStatusEnum.APPROVED.getId()))
                                        .thenReturn(Optional.of(orderStatusEntityApproved));

                        when(orderRepository.save(any(Order.class)))
                                        .thenThrow(new RuntimeException("DB error"));

                        assertThrows(RuntimeException.class,
                                        () -> orderService.approve(order.getId(), loggedInUser));
                }

                @Test
                void shouldThrowWhenPublishFails() {
                        order.setOrderStatus(orderStatusEntity);
                        order.setItems(List.of(orderItem));

                        when(orderRepository.findById(order.getId()))
                                        .thenReturn(Optional.of(order));

                        when(orderStatusRepository.findById(OrderStatusEnum.APPROVED.getId()))
                                        .thenReturn(Optional.of(orderStatusEntityApproved));

                        when(orderRepository.save(any(Order.class)))
                                        .thenAnswer(inv -> inv.getArgument(0));

                        doThrow(new RuntimeException("log error"))
                                        .when(logPublisherService)
                                        .publish(any(), any(), any(), any());

                        assertThrows(RuntimeException.class,
                                        () -> orderService.approve(order.getId(), loggedInUser));

                        verify(logPublisherService).publish(any(), any(), any(), any());
                }
        }

}
