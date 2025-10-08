package com.github.renancvitor.inventory.service.movement;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.renancvitor.inventory.domain.entity.movement.Movement;
import com.github.renancvitor.inventory.domain.entity.movement.MovementTypeEntity;
import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.dto.movement.MovementDetailData;
import com.github.renancvitor.inventory.dto.movement.MovementLogData;
import com.github.renancvitor.inventory.dto.movement.MovementRequest;
import com.github.renancvitor.inventory.exception.factory.NotFoundExceptionFactory;
import com.github.renancvitor.inventory.exception.types.product.InsufficientStockException;
import com.github.renancvitor.inventory.exception.types.product.InvalidQuantityException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.repository.MovementRepository;
import com.github.renancvitor.inventory.repository.MovementTypeRepository;
import com.github.renancvitor.inventory.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovementService {

    private final MovementRepository movementRepository;
    private final MovementTypeRepository movementTypeRepository;
    private final SystemLogPublisherService logPublisherService;
    private final ProductRepository productRepository;

    @Transactional
    public MovementDetailData output(Long productId, MovementRequest request, User loggedInUser) {
        return handleMovement(productId, request, MovementTypeEnum.OUTPUT, loggedInUser);
    }

    @Transactional
    public MovementDetailData input(Long productId, MovementRequest request, User loggedInUser) {
        return handleMovement(productId, request, MovementTypeEnum.INPUT, loggedInUser);
    }

    private MovementDetailData handleMovement(Long productId, MovementRequest request, MovementTypeEnum movementType,
            User loggedInUser) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> NotFoundExceptionFactory.product(productId));

        MovementTypeEntity movementTypeEntity = movementTypeRepository.findById(movementType.getId())
                .orElseThrow(() -> NotFoundExceptionFactory.movementType(movementType.getId()));

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

        movementRepository.save(movement);

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
