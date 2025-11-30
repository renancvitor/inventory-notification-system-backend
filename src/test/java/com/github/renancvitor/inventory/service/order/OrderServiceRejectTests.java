package com.github.renancvitor.inventory.service.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;
import com.github.renancvitor.inventory.domain.entity.user.exception.AccessDeniedException;
import com.github.renancvitor.inventory.exception.types.common.EntityNotFoundException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderServiceRejectTests {

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
        void shouldRejectOrderSuccessfully() {
            order.setOrderStatus(orderStatusEntity);
            order.setRequestedBy(loggedInUser);

            OrderStatusEntity rejectedStatus = TestEntityFactory.createStatusRejected();

            when(orderRepository.findById(order.getId()))
                    .thenReturn(Optional.of(order));

            when(orderStatusRepository.findById(OrderStatusEnum.REJECTED.getId()))
                    .thenReturn(Optional.of(rejectedStatus));

            when(orderRepository.save(any(Order.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            OrderDetailData result = orderService.reject(order.getId(), loggedInUser);

            assertNotNull(result);
            assertEquals(OrderStatusEnum.REJECTED.getDisplayName(), result.status());

            verify(authenticationService).authorize(List.of(UserTypeEnum.ADMIN, UserTypeEnum.PRODUCT_MANAGER));
            verify(orderRepository).findById(1L);
            verify(orderStatusRepository).findById(OrderStatusEnum.REJECTED.getId());
            verify(orderRepository).save(any(Order.class));
            verify(logPublisherService).publish(
                    eq("ORDER_REJECTED"),
                    contains(loggedInUser.getUsername()),
                    any(OrderLogData.class),
                    any(OrderLogData.class));
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldThrowWhenUserIsNotAuthorized() {
            doThrow(new AccessDeniedException("Forbidden"))
                    .when(authenticationService)
                    .authorize(any());

            assertThrows(AccessDeniedException.class,
                    () -> orderService.reject(order.getId(), loggedInUser));

            verify(authenticationService).authorize(any());
        }

        @Test
        void shouldThrowWhenOrderNotFound() {
            doNothing().when(authenticationService).authorize(any());

            when(orderRepository.findById(order.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> orderService.reject(order.getId(), loggedInUser));

            verify(orderRepository).findById(order.getId());
        }

        @Test
        void shouldThrowWhenStatusIsNotPending() {
            orderStatusEntity = TestEntityFactory.createStatusApproved();

            order.setOrderStatus(orderStatusEntity);
            order.setRequestedBy(loggedInUser);

            doNothing().when(authenticationService).authorize(any());

            when(orderRepository.findById(order.getId()))
                    .thenReturn(Optional.of(order));

            assertThrows(OrderStatusException.class,
                    () -> orderService.reject(order.getId(), loggedInUser));

            verify(orderRepository).findById(order.getId());
        }

        @Test
        void shouldThrowWhenRejectedStatusNotFound() {
            order.setRequestedBy(loggedInUser);
            order.setOrderStatus(orderStatusEntity);

            doNothing().when(authenticationService).authorize(any());

            when(orderRepository.findById(order.getId()))
                    .thenReturn(Optional.of(order));

            when(orderStatusRepository.findById(OrderStatusEnum.REJECTED.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> orderService.reject(order.getId(), loggedInUser));

            verify(orderStatusRepository)
                    .findById(OrderStatusEnum.REJECTED.getId());
        }

        @Test
        void shouldThrowWhenLoggedUserIsNull() {
            order.setOrderStatus(orderStatusEntity);
            order.setRequestedBy(loggedInUser);

            doNothing().when(authenticationService).authorize(any());

            when(orderRepository.findById(order.getId()))
                    .thenReturn(Optional.of(order));

            assertThrows(EntityNotFoundException.class,
                    () -> orderService.reject(order.getId(), null));

            verify(orderRepository).findById(order.getId());
        }

        @Test
        void shouldThrowWhenSaveFails() {
            order.setRequestedBy(loggedInUser);
            order.setOrderStatus(orderStatusEntity);

            doNothing().when(authenticationService).authorize(any());

            when(orderRepository.findById(order.getId()))
                    .thenReturn(Optional.of(order));

            OrderStatusEntity orderStatusEntityRejected = TestEntityFactory.createStatusRejected();

            when(orderStatusRepository.findById(OrderStatusEnum.REJECTED.getId()))
                    .thenReturn(Optional.of(orderStatusEntityRejected));

            when(orderRepository.save(any(Order.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class,
                    () -> orderService.reject(order.getId(), loggedInUser));

            verify(orderRepository).save(any(Order.class));
        }

        @Test
        void shouldThrowWhenPublishFails() {
            order.setRequestedBy(loggedInUser);
            order.setOrderStatus(orderStatusEntity);

            doNothing().when(authenticationService).authorize(any());

            when(orderRepository.findById(order.getId()))
                    .thenReturn(Optional.of(order));

            OrderStatusEntity orderStatusEntityRejected = TestEntityFactory.createStatusRejected();

            when(orderStatusRepository.findById(OrderStatusEnum.REJECTED.getId()))
                    .thenReturn(Optional.of(orderStatusEntityRejected));

            when(orderRepository.save(any(Order.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            doThrow(new RuntimeException("log error"))
                    .when(logPublisherService)
                    .publish(any(), any(), any(), any());

            assertThrows(RuntimeException.class,
                    () -> orderService.reject(order.getId(), loggedInUser));

            verify(logPublisherService).publish(any(), any(), any(), any());
        }
    }

}
