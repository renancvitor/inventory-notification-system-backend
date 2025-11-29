package com.github.renancvitor.inventory.service.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.order.dto.OrderDetailData;
import com.github.renancvitor.inventory.application.order.repository.OrderItemRepository;
import com.github.renancvitor.inventory.application.order.repository.OrderRepository;
import com.github.renancvitor.inventory.application.order.repository.OrderStatusRepository;
import com.github.renancvitor.inventory.application.order.service.OrderService;
import com.github.renancvitor.inventory.domain.entity.movement.Movement;
import com.github.renancvitor.inventory.domain.entity.order.Order;
import com.github.renancvitor.inventory.domain.entity.order.OrderItem;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderServiceListTests {

    @Mock
    private OrderRepository orderRepository;

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

    @BeforeEach
    void setup() {
        orderItem = TestEntityFactory.createOrderItem();
        order = TestEntityFactory.createOrder();
        loggedInUser = TestEntityFactory.createUser();
        product = TestEntityFactory.createProduct();
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldListAllOrders() {
            Page<Order> page = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(
                    ArgumentMatchers.<Specification<Order>>any(),
                    any(Pageable.class))).thenReturn(page);

            Page<OrderDetailData> result = orderService.list(
                    PageRequest.of(0, 10),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            assertEquals(1, result.getTotalElements());

            verify(orderRepository)
                    .findAll(ArgumentMatchers.<Specification<Order>>any(),
                            eq(PageRequest.of(0, 10)));
        }

        @Test
        void shouldListOrdersFilteredByStatus() {
            Page<Order> page = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(
                    ArgumentMatchers.<Specification<Order>>any(),
                    any(Pageable.class))).thenReturn(page);

            Page<OrderDetailData> result = orderService.list(
                    PageRequest.of(0, 10),
                    null,
                    1,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            assertEquals(1, result.getTotalElements());

            verify(orderRepository)
                    .findAll(ArgumentMatchers.<Specification<Order>>any(),
                            eq(PageRequest.of(0, 10)));
        }

        @Test
        void shouldListOrdersFilteredByRequestedBy() {
            Page<Order> page = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(
                    ArgumentMatchers.<Specification<Order>>any(),
                    any(Pageable.class))).thenReturn(page);

            Page<OrderDetailData> result = orderService.list(
                    PageRequest.of(0, 10),
                    null,
                    null,
                    10L,
                    null,
                    null,
                    null,
                    null,
                    null);

            assertEquals(1, result.getTotalElements());

            verify(orderRepository)
                    .findAll(ArgumentMatchers.<Specification<Order>>any(),
                            eq(PageRequest.of(0, 10)));
        }

        @Test
        void shouldListOrdersFilteredByApprovedBy() {
            Page<Order> page = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(
                    ArgumentMatchers.<Specification<Order>>any(),
                    any(Pageable.class))).thenReturn(page);

            Page<OrderDetailData> result = orderService.list(
                    PageRequest.of(0, 10),
                    null,
                    null,
                    null,
                    20L,
                    null,
                    null,
                    null,
                    null);

            assertEquals(1, result.getTotalElements());

            verify(orderRepository)
                    .findAll(ArgumentMatchers.<Specification<Order>>any(),
                            eq(PageRequest.of(0, 10)));
        }

        @Test
        void shouldListOrdersFilteredByRejectedBy() {
            Page<Order> page = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(
                    ArgumentMatchers.<Specification<Order>>any(),
                    any(Pageable.class))).thenReturn(page);

            Page<OrderDetailData> result = orderService.list(
                    PageRequest.of(0, 10),
                    null,
                    null,
                    null,
                    null,
                    30L,
                    null,
                    null,
                    null);

            assertEquals(1, result.getTotalElements());

            verify(orderRepository)
                    .findAll(ArgumentMatchers.<Specification<Order>>any(),
                            eq(PageRequest.of(0, 10)));
        }

        @Test
        void shouldListOrdersFilteredByCreatedAt() {
            Page<Order> page = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(
                    ArgumentMatchers.<Specification<Order>>any(),
                    any(Pageable.class))).thenReturn(page);

            LocalDateTime createdAt = LocalDateTime.now();

            Page<OrderDetailData> result = orderService.list(
                    PageRequest.of(0, 10),
                    null,
                    null,
                    null,
                    null,
                    null,
                    createdAt,
                    null,
                    null);

            assertEquals(1, result.getTotalElements());

            verify(orderRepository)
                    .findAll(ArgumentMatchers.<Specification<Order>>any(),
                            eq(PageRequest.of(0, 10)));
        }

        @Test
        void shouldListOrdersFilteredByUpdatedAt() {
            Page<Order> page = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(
                    ArgumentMatchers.<Specification<Order>>any(),
                    any(Pageable.class))).thenReturn(page);

            LocalDateTime updatedAt = LocalDateTime.now();

            Page<OrderDetailData> result = orderService.list(
                    PageRequest.of(0, 10),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    updatedAt,
                    null);

            assertEquals(1, result.getTotalElements());

            verify(orderRepository)
                    .findAll(ArgumentMatchers.<Specification<Order>>any(),
                            eq(PageRequest.of(0, 10)));
        }

        @Test
        void shouldListOrdersFilteredByTotalValue() {
            Page<Order> page = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(
                    ArgumentMatchers.<Specification<Order>>any(),
                    any(Pageable.class))).thenReturn(page);

            BigDecimal totalValue = BigDecimal.valueOf(120);

            Page<OrderDetailData> result = orderService.list(
                    PageRequest.of(0, 10),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    totalValue);

            assertEquals(1, result.getTotalElements());

            verify(orderRepository)
                    .findAll(ArgumentMatchers.<Specification<Order>>any(),
                            eq(PageRequest.of(0, 10)));
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldThrowWhenRepositoryFails() {
            when(orderRepository.findAll(
                    ArgumentMatchers.<Specification<Order>>any(),
                    any(Pageable.class))).thenThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () -> {
                orderService.list(
                        PageRequest.of(0, 10),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
            });

            verify(orderRepository)
                    .findAll(ArgumentMatchers.<Specification<Order>>any(),
                            eq(PageRequest.of(0, 10)));
        }

        @Test
        void shouldThrowWhenPageableIsInvalid() {
            assertThrows(IllegalArgumentException.class, () -> {
                orderService.list(
                        PageRequest.of(-1, 10),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
            });
        }

        @Test
        void shouldThrowWhenRepositoryReturnsNullPage() {
            when(orderRepository.findAll(
                    ArgumentMatchers.<Specification<Order>>any(),
                    any(Pageable.class))).thenReturn(null);

            assertThrows(NullPointerException.class, () -> {
                orderService.list(
                        PageRequest.of(0, 10),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
            });

            verify(orderRepository)
                    .findAll(ArgumentMatchers.<Specification<Order>>any(),
                            eq(PageRequest.of(0, 10)));
        }

        @Test
        void shouldThrowWhenMappingFailsInsidePageMap() {
            Page<Order> brokenPage = new PageImpl<>(Arrays.asList((Order) null));

            when(orderRepository.findAll(
                    ArgumentMatchers.<Specification<Order>>any(),
                    any(Pageable.class))).thenReturn(brokenPage);

            assertThrows(NullPointerException.class, () -> {
                orderService.list(
                        PageRequest.of(0, 10),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
            });

            verify(orderRepository)
                    .findAll(ArgumentMatchers.<Specification<Order>>any(),
                            eq(PageRequest.of(0, 10)));
        }

        @Test
        void shouldThrowWhenMovementDetailMappingFails() {
            Movement brokenMovement = new Movement();
            brokenMovement.setId(1L);
            brokenMovement.setProduct(product);
            brokenMovement.setQuantity(10);

            order.setMovements(List.of(brokenMovement));

            Page<Order> page = new PageImpl<>(List.of(order));

            when(orderRepository.findAll(
                    ArgumentMatchers.<Specification<Order>>any(),
                    any(Pageable.class))).thenReturn(page);

            assertThrows(NullPointerException.class, () -> {
                orderService.list(
                        PageRequest.of(0, 10),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
            });

            verify(orderRepository)
                    .findAll(ArgumentMatchers.<Specification<Order>>any(),
                            eq(PageRequest.of(0, 10)));
        }
    }

}
