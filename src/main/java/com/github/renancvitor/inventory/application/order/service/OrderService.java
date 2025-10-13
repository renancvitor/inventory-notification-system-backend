package com.github.renancvitor.inventory.application.order.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.renancvitor.inventory.application.movement.dto.MovementOrderRequest;
import com.github.renancvitor.inventory.application.movement.repository.MovementTypeRepository;
import com.github.renancvitor.inventory.application.movement.service.MovementService;
import com.github.renancvitor.inventory.application.order.dto.OrderCreationData;
import com.github.renancvitor.inventory.application.order.dto.OrderDetailData;
import com.github.renancvitor.inventory.application.order.dto.OrderLogData;
import com.github.renancvitor.inventory.application.order.repository.OrderRepository;
import com.github.renancvitor.inventory.application.order.repository.OrderStatusRepository;
import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.domain.entity.movement.Movement;
import com.github.renancvitor.inventory.domain.entity.movement.MovementTypeEntity;
import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;
import com.github.renancvitor.inventory.domain.entity.order.Order;
import com.github.renancvitor.inventory.domain.entity.order.enums.OrderStatusEnum;
import com.github.renancvitor.inventory.domain.entity.order.exceptions.OrderStatusException;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.exception.factory.NotFoundExceptionFactory;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MovementTypeRepository movementTypeRepository;
    private final ProductRepository productRepository;
    private final MovementService movementService;
    private final OrderStatusRepository orderStatusRepository;
    private final SystemLogPublisherService logPublisherService;

    @Transactional
    public OrderDetailData create(OrderCreationData data, User loggedInUser) {
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setRequestedBy(loggedInUser);
        order.setDescription(data.description());

        List<Movement> movements = data.movements().stream()
                .map(req -> convertToEntity(req, loggedInUser, order))
                .toList();

        order.setMovements(movements);

        orderRepository.save(order);

        OrderLogData newData = OrderLogData.fromEntity(order);

        logPublisherService.publish(
                "ORDER_CREATED",
                "Pedido criado pelo usuário " + loggedInUser.getUsername(),
                null,
                newData);

        return new OrderDetailData(order);
    }

    @Transactional
    public OrderDetailData approvedOrder(Long orderId, User loggedInUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> NotFoundExceptionFactory.order(orderId));

        if (!order.getOrderStatus().getId().equals(OrderStatusEnum.PENDING.getId())) {
            throw new OrderStatusException("Status 'Pendente' obrigatório para esta ação.");
        }

        order.getMovements().forEach(movement -> {
            MovementOrderRequest request = new MovementOrderRequest(
                    movement.getProduct().getId(),
                    movement.getMovementType().getId(),
                    movement.getQuantity(),
                    movement.getUnitPrice());

            MovementTypeEnum type = MovementTypeEnum.fromId(movement.getMovementType().getId());

            if (type == MovementTypeEnum.INPUT) {
                movementService.input(request, loggedInUser, order);
            } else {
                movementService.output(request, loggedInUser, order);
            }
        });

        order.setOrderStatus(
                orderStatusRepository.findById(OrderStatusEnum.APPROVED.getId())
                        .orElseThrow(() -> new OrderStatusException("Status 'Aprovado' não encontrado.")));
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        OrderLogData newData = OrderLogData.fromEntity(order);

        logPublisherService.publish(
                "ORDER_APPROVED",
                "Pedido aprovado pelo usuário " + loggedInUser.getUsername(),
                null,
                newData);

        return new OrderDetailData(order);
    }

    private Movement convertToEntity(MovementOrderRequest request, User loggedInUser, Order order) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> NotFoundExceptionFactory.product(request.productId()));

        MovementTypeEntity movementType = movementTypeRepository.findById(request.movementTypeId())
                .orElseThrow(() -> NotFoundExceptionFactory.movementType(request.movementTypeId()));

        Movement movement = new Movement();
        movement.setProduct(product);
        movement.setMovementType(movementType);
        movement.setQuantity(request.quantity());
        movement.setUnitPrice(request.unitPrice());
        movement.setMovementationDate(LocalDateTime.now());
        movement.setUser(loggedInUser);
        movement.setOrder(order);

        return movement;
    }

}
