package com.github.renancvitor.inventory.service.movement;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.renancvitor.inventory.application.email.dto.EmailRequest;
import com.github.renancvitor.inventory.application.email.service.EmailService;
import com.github.renancvitor.inventory.application.movement.repository.MovementRepository;
import com.github.renancvitor.inventory.application.movement.service.MovementReportService;
import com.github.renancvitor.inventory.domain.entity.movement.Movement;
import com.github.renancvitor.inventory.domain.entity.movement.MovementTypeEntity;
import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;
import com.github.renancvitor.inventory.utils.TestEntityFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MovementReportServiceTests {

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private MovementReportService movementReportService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(
                movementReportService,
                "recipients",
                "email1@teste.com,email2@teste.com");
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldNotSendEmailWhenNoMovements() {
            when(movementRepository.findByMovementationDateBetween(any(), any()))
                    .thenReturn(List.of());

            movementReportService.sendDailyReport();

            verify(emailService, times(0)).sendEmail(any(), any());
        }

        @Test
        void shouldSendEmailWhenMovementsExist() {
            Movement movement = TestEntityFactory.createMovement();
            movement.setMovementType(MovementTypeEntity.fromEnum(MovementTypeEnum.INPUT));
            when(movementRepository.findByMovementationDateBetween(any(), any()))
                    .thenReturn(List.of(movement));

            movementReportService.sendDailyReport();

            verify(emailService).sendEmail(any(EmailRequest.class), isNull());
        }

        @Test
        void shouldSplitRecipientsCorrectly() {
            ReflectionTestUtils.setField(
                    movementReportService,
                    "recipients",
                    "a@a.com,  b@b.com , , c@c.com");

            Movement movement = TestEntityFactory.createMovement();
            movement.setMovementType(MovementTypeEntity.fromEnum(MovementTypeEnum.INPUT));
            when(movementRepository.findByMovementationDateBetween(any(), any()))
                    .thenReturn(List.of(movement));

            ArgumentCaptor<EmailRequest> captor = ArgumentCaptor.forClass(EmailRequest.class);

            movementReportService.sendDailyReport();

            verify(emailService).sendEmail(captor.capture(), any());
            List<String> emails = captor.getValue().recipient();

            assertEquals(List.of("a@a.com", "b@b.com", "c@c.com"), emails);
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldThrowExceptionWhenRecipientsIsEmpty() {
            ReflectionTestUtils.setField(
                    movementReportService,
                    "recipients",
                    "");

            Movement movement = TestEntityFactory.createMovement();
            movement.setMovementType(MovementTypeEntity.fromEnum(MovementTypeEnum.INPUT));
            when(movementRepository.findByMovementationDateBetween(any(), any()))
                    .thenReturn(List.of(movement));

            assertThatThrownBy(() -> movementReportService.sendDailyReport())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Nenhum e-mail válido configurado");
        }
    }

}
