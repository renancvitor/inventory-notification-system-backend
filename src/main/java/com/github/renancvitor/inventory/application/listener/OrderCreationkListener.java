package com.github.renancvitor.inventory.application.listener;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.application.order.dto.OrderCreationData;
import com.github.renancvitor.inventory.application.order.dto.OrderItemRequest;
import com.github.renancvitor.inventory.application.order.repository.OrderRepository;
import com.github.renancvitor.inventory.application.order.service.OrderService;
import com.github.renancvitor.inventory.application.user.repository.UserRepository;
import com.github.renancvitor.inventory.domain.entity.order.Order;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.events.OrderCreationEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCreationkListener {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;

    @EventListener
    public void handler(OrderCreationEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow();
        User loggedUser = userRepository.findById(event.userId())
                .orElseThrow();

        List<OrderItemRequest> items = order.getItems().stream()
                .map(item -> new OrderItemRequest(
                        item.getProduct().getId(),
                        item.getMovementType().getId(),
                        item.getQuantity(),
                        item.getUnitPrice()))
                .toList();

        OrderCreationData orderCreationData = new OrderCreationData(order.getDescription(), items);

        orderService.create(orderCreationData, loggedUser);
    }

}
