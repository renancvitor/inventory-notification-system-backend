package com.github.renancvitor.inventory.service.movement;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.renancvitor.inventory.application.movement.dto.MovementDetailData;
import com.github.renancvitor.inventory.application.movement.dto.MovementOrderRequest;
import com.github.renancvitor.inventory.application.movement.repository.MovementRepository;
import com.github.renancvitor.inventory.application.movement.repository.MovementTypeRepository;
import com.github.renancvitor.inventory.application.movement.service.MovementService;
import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.application.product.service.StockMonitorService;
import com.github.renancvitor.inventory.domain.entity.movement.Movement;
import com.github.renancvitor.inventory.domain.entity.movement.MovementTypeEntity;
import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.product.exception.InsufficientStockException;
import com.github.renancvitor.inventory.domain.entity.product.exception.InvalidQuantityException;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
class MovementServiceTests {

    @Mock
    private MovementRepository movementRepository;
    @Mock
    private MovementTypeRepository movementTypeRepository;
    @Mock
    private SystemLogPublisherService logPublisherService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockMonitorService stockMonitorService;

    @InjectMocks
    private MovementService movementService;

    private Product product;
    private MovementTypeEntity movementTypeEntityInput;
    private MovementTypeEntity movementTypeEntityOutput;
    private User user;
    private UserTypeEntity userTypeEntity;

    @BeforeEach
    void setup() {
        product = TestEntityFactory.createProduct();
        product.setStock(20);

        movementTypeEntityInput = new MovementTypeEntity();
        movementTypeEntityInput.setId(MovementTypeEnum.INPUT.getId());
        movementTypeEntityInput.setMovementTypeName(MovementTypeEnum.INPUT.name());

        movementTypeEntityOutput = new MovementTypeEntity();
        movementTypeEntityOutput.setId(MovementTypeEnum.OUTPUT.getId());
        movementTypeEntityOutput.setMovementTypeName(MovementTypeEnum.OUTPUT.name());

        userTypeEntity = TestEntityFactory.createUserTypeAdmin();

        user = TestEntityFactory.createUser();
        user.setUserType(userTypeEntity);
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldHandleInputMovement() {
            MovementOrderRequest request = new MovementOrderRequest(
                    product.getId(),
                    MovementTypeEnum.INPUT.getId(),
                    5,
                    new BigDecimal("3.00"));

            when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.of(product));

            when(movementTypeRepository.findById(MovementTypeEnum.INPUT.getId()))
                    .thenReturn(Optional.of(movementTypeEntityInput));

            when(movementRepository.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0, Movement.class);
                        m.setId(999L);
                        return m;
                    });

            MovementDetailData result = movementService.input(request, user, null);

            assertEquals(999L, result.id());
            assertEquals(25, product.getStock());

            verify(productRepository).save(product);
            verify(stockMonitorService).handleLowStock(product, user);
            verify(movementRepository).save(any(Movement.class));
            verify(logPublisherService).publish(eq("PRODUCT_MOVEMENT"), anyString(), isNull(), any());
        }

        @Test
        void shouldHandleOutputMovement() {
            MovementOrderRequest request = new MovementOrderRequest(
                    product.getId(),
                    MovementTypeEnum.OUTPUT.getId(),
                    5,
                    new BigDecimal("3.00"));

            when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.of(product));

            when(movementTypeRepository.findById(MovementTypeEnum.OUTPUT.getId()))
                    .thenReturn(Optional.of(movementTypeEntityOutput));

            when(movementRepository.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0, Movement.class);
                        m.setId(1000L);
                        return m;
                    });

            MovementDetailData result = movementService.output(request, user, null);

            assertEquals(1000L, result.id());
            assertEquals(15, product.getStock());

            verify(productRepository).save(product);
            verify(stockMonitorService).handleLowStock(product, user);
            verify(movementRepository).save(any(Movement.class));
            verify(logPublisherService).publish(eq("PRODUCT_MOVEMENT"), anyString(), isNull(), any());
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldThrowWhenProductNotFound() {
            MovementOrderRequest request = new MovementOrderRequest(
                    999L, MovementTypeEnum.INPUT.getId(), 5, BigDecimal.ONE);

            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> movementService.input(request, user, null))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void shouldThrowWhenMovementTypeNotFound() {
            MovementOrderRequest request = new MovementOrderRequest(
                    product.getId(), 999, 5, BigDecimal.ONE);

            when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.of(product));

            when(movementTypeRepository.findById(999))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> movementService.input(request, user, null))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void shouldThrowWhenQuantityIsZeroOrNegative() {
            MovementOrderRequest request = new MovementOrderRequest(
                    product.getId(),
                    MovementTypeEnum.INPUT.getId(),
                    0,
                    BigDecimal.ONE);

            when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.of(product));

            when(movementTypeRepository.findById(MovementTypeEnum.INPUT.getId()))
                    .thenReturn(Optional.of(movementTypeEntityInput));

            assertThatThrownBy(() -> movementService.input(request, user, null))
                    .isInstanceOf(InvalidQuantityException.class);
        }

        @Test
        void shouldThrowWhenStockIsInsufficientForOutput() {
            MovementOrderRequest request = new MovementOrderRequest(
                    product.getId(),
                    MovementTypeEnum.OUTPUT.getId(),
                    50,
                    BigDecimal.ONE);

            when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.of(product));

            when(movementTypeRepository.findById(MovementTypeEnum.OUTPUT.getId()))
                    .thenReturn(Optional.of(movementTypeEntityOutput));

            assertThatThrownBy(() -> movementService.output(request, user, null))
                    .isInstanceOf(InsufficientStockException.class);
        }
    }

}
