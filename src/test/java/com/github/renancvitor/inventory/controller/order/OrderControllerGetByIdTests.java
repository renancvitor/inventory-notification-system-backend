package com.github.renancvitor.inventory.controller.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
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
import com.github.renancvitor.inventory.application.order.service.OrderService;
import com.github.renancvitor.inventory.domain.entity.order.Order;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderControllerGetByIdTests {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private Order order;

    @BeforeEach
    void setup() {
        order = TestEntityFactory.createOrder();
    }

    @Test
    void shouldReturnOrderById() {
        Long id = 1L;
        OrderDetailData detailData = new OrderDetailData(order);

        when(orderService.getById(id)).thenReturn(detailData);

        ResponseEntity<OrderDetailData> response = orderController.getById(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(detailData);

        verify(orderService).getById(id);
    }

}
