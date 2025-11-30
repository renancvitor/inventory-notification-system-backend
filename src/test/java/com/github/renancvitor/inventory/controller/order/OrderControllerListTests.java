package com.github.renancvitor.inventory.controller.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.order.controller.OrderController;
import com.github.renancvitor.inventory.application.order.dto.OrderDetailData;
import com.github.renancvitor.inventory.application.order.dto.OrderFilter;
import com.github.renancvitor.inventory.application.order.repository.OrderItemRepository;
import com.github.renancvitor.inventory.application.order.repository.OrderRepository;
import com.github.renancvitor.inventory.application.order.repository.OrderStatusRepository;
import com.github.renancvitor.inventory.application.order.service.OrderService;
import com.github.renancvitor.inventory.domain.entity.order.Order;
import com.github.renancvitor.inventory.domain.entity.order.OrderItem;
import com.github.renancvitor.inventory.domain.entity.order.OrderStatusEntity;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.util.CustomPage;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderControllerListTests {

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

    @BeforeEach
    void setup() {
        orderItem = TestEntityFactory.createOrderItem();
        order = TestEntityFactory.createOrder();
        loggedInUser = TestEntityFactory.createUser();
        product = TestEntityFactory.createProduct();
        orderStatusEntity = TestEntityFactory.createStatusPending();
    }

    @Nested
    class PositiveCases {
        @SuppressWarnings("null")
        @Test
        void shouldListOrdersWithAllFilters() {
            Pageable pageable = PageRequest.of(0, 10);

            loggedInUser.setId(1L);
            loggedInUser.setId(2L);
            loggedInUser.setId(3L);
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();
            BigDecimal totalValue = BigDecimal.valueOf(150.00);

            OrderFilter filter = mock(OrderFilter.class);
            when(filter.orderStatusId()).thenReturn(orderStatusEntity.getId());
            when(filter.requestedBy()).thenReturn(1L);
            when(filter.approvedBy()).thenReturn(2L);
            when(filter.rejectedBy()).thenReturn(3L);
            when(filter.createdAt()).thenReturn(createdAt);
            when(filter.updatedAt()).thenReturn(updatedAt);
            when(filter.totalValue()).thenReturn(totalValue);

            OrderDetailData detailData = mock(OrderDetailData.class);

            Page<OrderDetailData> page = new PageImpl<>(
                    List.of(detailData),
                    pageable,
                    1);

            when(orderService.list(
                    eq(pageable),
                    eq(loggedInUser),
                    eq(orderStatusEntity.getId()),
                    eq(1L),
                    eq(2L),
                    eq(3L),
                    eq(createdAt),
                    eq(updatedAt),
                    eq(totalValue)))
                    .thenReturn(page);

            ResponseEntity<CustomPage<OrderDetailData>> response = orderController.list(
                    pageable,
                    loggedInUser,
                    filter);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().content()).hasSize(1);

            verify(orderService).list(
                    eq(pageable),
                    eq(loggedInUser),
                    eq(orderStatusEntity.getId()),
                    eq(1L),
                    eq(2L),
                    eq(3L),
                    eq(createdAt),
                    eq(updatedAt),
                    eq(totalValue));
        }

        @SuppressWarnings("null")
        @Test
        void shouldListOrdersWithoutFilters() {
            Pageable pageable = PageRequest.of(0, 10);

            OrderFilter filter = mock(OrderFilter.class);
            when(filter.orderStatusId()).thenReturn(null);
            when(filter.requestedBy()).thenReturn(null);
            when(filter.approvedBy()).thenReturn(null);
            when(filter.rejectedBy()).thenReturn(null);
            when(filter.createdAt()).thenReturn(null);
            when(filter.updatedAt()).thenReturn(null);
            when(filter.totalValue()).thenReturn(null);

            Page<OrderDetailData> emptyPage = new PageImpl<>(
                    List.of(),
                    pageable,
                    0);

            when(orderService.list(
                    eq(pageable),
                    eq(loggedInUser),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull()))
                    .thenReturn(emptyPage);

            ResponseEntity<CustomPage<OrderDetailData>> response = orderController.list(
                    pageable,
                    loggedInUser,
                    filter);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().content()).isEmpty();

            verify(orderService).list(
                    eq(pageable),
                    eq(loggedInUser),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull());
        }

        @SuppressWarnings("null")
        @Test
        void shouldApplyCustomPaginationAndSorting() {
            Pageable customPageable = PageRequest.of(
                    2,
                    5,
                    Sort.by("id").descending());

            OrderFilter filter = mock(OrderFilter.class);
            when(filter.orderStatusId()).thenReturn(null);
            when(filter.requestedBy()).thenReturn(null);
            when(filter.approvedBy()).thenReturn(null);
            when(filter.rejectedBy()).thenReturn(null);
            when(filter.createdAt()).thenReturn(null);
            when(filter.updatedAt()).thenReturn(null);
            when(filter.totalValue()).thenReturn(null);

            Page<OrderDetailData> page = new PageImpl<>(
                    List.of(),
                    customPageable,
                    0);

            when(orderService.list(
                    eq(customPageable),
                    eq(loggedInUser),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull()))
                    .thenReturn(page);

            ResponseEntity<CustomPage<OrderDetailData>> response = orderController.list(
                    customPageable,
                    loggedInUser,
                    filter);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().page()).isEqualTo(2);
            assertThat(response.getBody().size()).isEqualTo(5);
            assertThat(response.getBody().totalElements()).isEqualTo(0L);

            verify(orderService).list(
                    eq(customPageable),
                    eq(loggedInUser),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull());
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldPropagateExceptionWhenServiceThrows() {
            Pageable pageable = PageRequest.of(0, 10);

            OrderFilter filter = mock(OrderFilter.class);

            RuntimeException exception = new RuntimeException("Unexpected error");

            when(orderService.list(
                    any(Pageable.class),
                    any(User.class),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any()))
                    .thenThrow(exception);

            assertThatThrownBy(() -> orderController.list(pageable, loggedInUser, filter))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Unexpected error");

            verify(orderService).list(
                    eq(pageable),
                    eq(loggedInUser),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any());
        }
    }

}
