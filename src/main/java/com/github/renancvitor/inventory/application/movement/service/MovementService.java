package com.github.renancvitor.inventory.application.movement.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.renancvitor.inventory.application.movement.dto.MovementDetailData;
import com.github.renancvitor.inventory.application.movement.dto.MovementLogData;
import com.github.renancvitor.inventory.application.movement.dto.MovementOrderRequest;
import com.github.renancvitor.inventory.application.movement.repository.MovementRepository;
import com.github.renancvitor.inventory.application.movement.repository.MovementTypeRepository;
import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.application.product.service.StockMonitorService;
import com.github.renancvitor.inventory.domain.entity.movement.Movement;
import com.github.renancvitor.inventory.domain.entity.movement.MovementTypeEntity;
import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;
import com.github.renancvitor.inventory.domain.entity.order.Order;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.product.exception.InsufficientStockException;
import com.github.renancvitor.inventory.domain.entity.product.exception.InvalidQuantityException;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.events.DomainEventPublisher;
import com.github.renancvitor.inventory.domain.events.StockBelowMinimumEvent;
import com.github.renancvitor.inventory.exception.factory.NotFoundExceptionFactory;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovementService {

    private final MovementRepository movementRepository;
    private final MovementTypeRepository movementTypeRepository;
    private final SystemLogPublisherService logPublisherService;
    private final ProductRepository productRepository;
    private final StockMonitorService stockMonitorService;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    public MovementDetailData output(MovementOrderRequest request, User loggedInUser, Order order) {
        return handleMovement(request, loggedInUser, order);
    }

    @Transactional
    public MovementDetailData input(MovementOrderRequest request, User loggedInUser, Order order) {
        return handleMovement(request, loggedInUser, order);
    }

    private MovementDetailData handleMovement(MovementOrderRequest request, User loggedInUser, Order order) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> NotFoundExceptionFactory.product(request.productId()));

        MovementTypeEntity movementTypeEntity = movementTypeRepository.findById(request.movementTypeId())
                .orElseThrow(() -> NotFoundExceptionFactory.movementType(request.movementTypeId()));

        MovementTypeEnum movementType = MovementTypeEnum.fromId(request.movementTypeId());

        int quantity = request.quantity();
        validateStock(product, quantity, movementType);

        int updatedStock = movementType.apply(product.getStock(), quantity);

        product.setStock(updatedStock);
        productRepository.save(product);

        Movement movement = new Movement();
        movement.setProduct(product);
        movement.setMovementType(movementTypeEntity);
        movement.setQuantity(quantity);
        movement.setUnitPrice(request.unitPrice());
        movement.setUser(loggedInUser);
        movement.setOrder(order);

        movementRepository.save(movement);

        stockMonitorService.handleLowStock(product, loggedInUser);

        if (product.isStockLow()) {
            domainEventPublisher.publish(
                    new StockBelowMinimumEvent(
                            product.getId(),
                            loggedInUser.getId(),
                            product.getStock(),
                            product.getMinimumStock(),
                            Instant.now()));
        }

        MovementLogData newData = MovementLogData.fromEntity(movement);

        logPublisherService.publish(
                "PRODUCT_MOVEMENT",
                "Produto movimentado pela usuário " + loggedInUser.getUsername(),
                null,
                newData);

        return new MovementDetailData(movement);
    }

    private void validateStock(Product product, int quantity, MovementTypeEnum movementType) {

        if (quantity <= 0) {
            throw new InvalidQuantityException(product.getProductName());
        }

        if (movementType == MovementTypeEnum.OUTPUT && product.getStock() < quantity) {
            throw new InsufficientStockException(product.getProductName());
        }
    }

}
