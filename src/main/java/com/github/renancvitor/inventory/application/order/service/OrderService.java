package com.github.renancvitor.inventory.application.order.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.github.renancvitor.inventory.application.authentication.service.AuthenticationService;
import com.github.renancvitor.inventory.application.movement.dto.MovementOrderRequest;
import com.github.renancvitor.inventory.application.movement.repository.MovementTypeRepository;
import com.github.renancvitor.inventory.application.movement.service.MovementService;
import com.github.renancvitor.inventory.application.order.dto.OrderCreationData;
import com.github.renancvitor.inventory.application.order.dto.OrderDetailData;
import com.github.renancvitor.inventory.application.order.dto.OrderLogData;
import com.github.renancvitor.inventory.application.order.dto.OrderUpdateData;
import com.github.renancvitor.inventory.application.order.repository.OrderRepository;
import com.github.renancvitor.inventory.application.order.repository.OrderSpecifications;
import com.github.renancvitor.inventory.application.order.repository.OrderStatusRepository;
import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.domain.entity.movement.Movement;
import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;
import com.github.renancvitor.inventory.domain.entity.order.Order;
import com.github.renancvitor.inventory.domain.entity.order.OrderItem;
import com.github.renancvitor.inventory.domain.entity.order.OrderStatusEntity;
import com.github.renancvitor.inventory.domain.entity.order.enums.OrderStatusEnum;
import com.github.renancvitor.inventory.domain.entity.order.exceptions.OrderStatusException;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;
import com.github.renancvitor.inventory.domain.entity.user.exception.AccessDeniedException;
import com.github.renancvitor.inventory.domain.events.EventTypes;
import com.github.renancvitor.inventory.domain.events.OrderCreationEvent;
import com.github.renancvitor.inventory.exception.factory.NotFoundExceptionFactory;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.infra.outbox.OutboxService;

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
    private final AuthenticationService authenticationService;
    private final OutboxService outboxService;

    public Page<OrderDetailData> list(Pageable pageable, User loggedInUser,
            Integer orderStatusId, Long requestedBy, Long approvedBy, Long rejectedBy,
            LocalDateTime createdAt, LocalDateTime updatedAt, BigDecimal totalValue) {

        Specification<Order> specification = Specification.unrestricted();

        if (orderStatusId != null) {
            specification = specification.and(OrderSpecifications.hasStatus(orderStatusId));
        }

        if (requestedBy != null) {
            specification = specification.and(OrderSpecifications.requestedBy(requestedBy));
        }

        if (approvedBy != null) {
            specification = specification.and(OrderSpecifications.approvedBy(approvedBy));
        }

        if (rejectedBy != null) {
            specification = specification.and(OrderSpecifications.rejectedBy(rejectedBy));
        }

        if (createdAt != null) {
            specification = specification.and(OrderSpecifications.createdAt(createdAt));
        }

        if (updatedAt != null) {
            specification = specification.and(OrderSpecifications.updatedAt(updatedAt));
        }

        if (totalValue != null) {
            specification = specification.and(OrderSpecifications.totalValue(totalValue));
        }

        return orderRepository.findAll(specification, pageable).map(OrderDetailData::new);
    }

    @Transactional
    public OrderDetailData create(OrderCreationData data, User loggedInUser) {
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setRequestedBy(loggedInUser);
        order.setDescription(data.description());

        OrderStatusEntity pendingStatus = orderStatusRepository.findById(OrderStatusEnum.PENDING.getId())
                .orElseThrow(() -> NotFoundExceptionFactory.orderStatus(OrderStatusEnum.PENDING.getId()));
        order.setOrderStatus(pendingStatus);

        List<OrderItem> items = data.items().stream()
                .map(dto -> {
                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setProduct(productRepository.findById(dto.productId()).orElseThrow());
                    item.setMovementType(movementTypeRepository.findById(dto.movementTypeId()).orElseThrow());
                    item.setQuantity(dto.quantity());
                    item.setUnitPrice(dto.unitPrice());
                    return item;
                }).toList();
        order.setItems(items);

        orderRepository.save(order);

        OrderLogData newData = OrderLogData.fromEntity(order);
        logPublisherService.publish(
                "ORDER_CREATED",
                "Pedido criado pelo usuário " + loggedInUser.getUsername(),
                null,
                newData);

        outboxService.saveEvent(
                "ORDER",
                order.getId(),
                EventTypes.ORDER_CREATED,
                "v1",
                new OrderCreationEvent(
                        order.getId(),
                        order.getRequestedBy().getId(),
                        order.getTotalValue(),
                        Instant.now()));

        return new OrderDetailData(order);
    }

    @Transactional
    public OrderDetailData update(Long id, OrderUpdateData data, User loggedInUser) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> NotFoundExceptionFactory.order(id));

        if (!order.getRequestedBy().equals(loggedInUser)) {
            throw new AccessDeniedException("Você só pode alterar o seu próprio pedido.");
        }

        if (!order.getOrderStatus().getId().equals(OrderStatusEnum.PENDING.getId())) {
            throw new OrderStatusException("Status 'Pendente' obrigatório para esta ação.");
        }

        OrderLogData oldData = OrderLogData.fromEntity(order);

        List<Movement> updateMovements = data.movements().stream()
                .map(movementRequest -> {
                    Product product = productRepository.findByIdAndActiveTrue(movementRequest.productId())
                            .orElseThrow(() -> NotFoundExceptionFactory.product(movementRequest.productId()));

                    Movement movement = new Movement();
                    movement.setProduct(product);
                    movement.setMovementType(movementTypeRepository.findById(movementRequest.movementTypeId())
                            .orElseThrow(
                                    () -> NotFoundExceptionFactory.movementType(movementRequest.movementTypeId())));
                    movement.setQuantity(movementRequest.quantity());
                    movement.setUnitPrice(movementRequest.unitPrice());
                    movement.updateTotalValue();
                    movement.setMovementationDate(LocalDateTime.now());
                    movement.setUser(loggedInUser);
                    movement.setOrder(order);

                    return movement;
                })
                .toList();

        order.setMovements(updateMovements);

        Order updatedOrder = orderRepository.save(order);
        OrderLogData newData = OrderLogData.fromEntity(updatedOrder);

        logPublisherService.publish("ORDER_UPDATED",
                "Pedido atualizado pelo usuário " + loggedInUser.getUsername(),
                oldData,
                newData);

        return new OrderDetailData(order);
    }

    @Transactional
    public OrderDetailData reject(Long id, User loggedInUser) {
        authenticationService.authorize(List.of(UserTypeEnum.ADMIN, UserTypeEnum.PRODUCT_MANAGER));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> NotFoundExceptionFactory.order(id));

        if (!order.getOrderStatus().getId().equals(OrderStatusEnum.PENDING.getId())) {
            throw new OrderStatusException("Status 'Pendente' obrigatório para esta ação.");
        }

        OrderLogData oldData = OrderLogData.fromEntity(order);

        OrderStatusEntity status = orderStatusRepository.findById(OrderStatusEnum.REJECTED.getId())
                .orElseThrow(() -> NotFoundExceptionFactory.orderStatus(OrderStatusEnum.REJECTED.getId()));
        order.setOrderStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        order.setRejectedBy(loggedInUser);

        Order updatedOrder = orderRepository.save(order);
        OrderLogData newData = OrderLogData.fromEntity(updatedOrder);

        logPublisherService.publish(
                "ORDER_REJECTED",
                "Pedido reprovado pelo usuário " + loggedInUser.getUsername(),
                oldData,
                newData);

        return new OrderDetailData(updatedOrder);
    }

    @Transactional
    public OrderDetailData approve(Long orderId, User loggedInUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> NotFoundExceptionFactory.order(orderId));

        if (!order.getOrderStatus().getId().equals(OrderStatusEnum.PENDING.getId())) {
            throw new OrderStatusException("Status 'Pendente' obrigatório para esta ação.");
        }

        OrderLogData oldData = OrderLogData.fromEntity(order);

        order.setOrderStatus(
                orderStatusRepository.findById(OrderStatusEnum.APPROVED.getId())
                        .orElseThrow(() -> new OrderStatusException("Status 'Aprovado' não encontrado.")));
        order.setUpdatedAt(LocalDateTime.now());
        order.setApprovedBy(loggedInUser);

        Order updatedOrder = orderRepository.save(order);

        for (OrderItem item : order.getItems()) {
            MovementOrderRequest request = new MovementOrderRequest(
                    item.getProduct().getId(),
                    item.getMovementType().getId(),
                    item.getQuantity(),
                    item.getUnitPrice());

            MovementTypeEnum type = MovementTypeEnum.fromId(item.getMovementType().getId());
            if (type == MovementTypeEnum.INPUT) {
                movementService.input(request, loggedInUser, order);
            } else {
                movementService.output(request, loggedInUser, order);
            }
        }

        OrderLogData newData = OrderLogData.fromEntity(updatedOrder);

        logPublisherService.publish(
                "ORDER_APPROVED",
                "Pedido aprovado pelo usuário " + loggedInUser.getUsername(),
                oldData,
                newData);

        return new OrderDetailData(updatedOrder);
    }

}
