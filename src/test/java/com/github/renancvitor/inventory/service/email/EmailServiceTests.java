package com.github.renancvitor.inventory.service.email;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.application.email.dto.EmailRequest;
import com.github.renancvitor.inventory.application.email.dto.EmailResponse;
import com.github.renancvitor.inventory.application.email.service.EmailService;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.exception.types.email.EmailException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;
import software.amazon.awssdk.services.ses.model.SesException;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class EmailServiceTests {

    @Mock
    private SesClient sesClient;

    @Mock
    private SystemLogPublisherService logPublisherService;

    @InjectMocks
    private EmailService emailService;

    private EmailRequest emailRequest;
    private User loggedInUser;

    @BeforeEach
    void setup() {
        emailRequest = new EmailRequest(
                List.of("destinatario@teste.com"),
                "Assunto de Teste",
                "<p>Mensagem em HTML</p>");

        loggedInUser = new User();
    }

    @Nested
    class PositiveCases {
        @Test
        void shouldSendEmailSuccessfully() {
            SendEmailResponse mockResponse = SendEmailResponse.builder()
                    .messageId("123-message-id")
                    .build();

            when(sesClient.sendEmail(any(SendEmailRequest.class)))
                    .thenReturn(mockResponse);

            EmailResponse response = emailService.sendEmail(emailRequest, loggedInUser);

            assertTrue(response.success());
            assertEquals("123-message-id", response.messageId());
            assertEquals("E-mail enviado com sucesso.", response.message());

            verify(logPublisherService).publish(
                    eq("EMAIL_SENT"),
                    eq("E-mail enviado com sucesso"),
                    isNull(),
                    any());
        }

        @Test
        void shouldHandleNullSesResponse() {
            when(sesClient.sendEmail(any(SendEmailRequest.class)))
                    .thenReturn(null);

            EmailResponse response = emailService.sendEmail(emailRequest, loggedInUser);

            assertFalse(response.success());
            assertNull(response.messageId());
            assertEquals("Falha ao enviar e-mail.", response.message());
        }
    }

    @Nested
    class NegativeCases {
        @Test
        void shouldThrowExceptionWhenRecipientIsEmpty() {
            EmailRequest invalidRequest = new EmailRequest(
                    List.of(),
                    "Assunto",
                    "<p>Mensagem</p>");

            assertThatThrownBy(() -> emailService.sendEmail(invalidRequest, loggedInUser))
                    .isInstanceOf(EmailException.class)
                    .hasMessageContaining("O destinatário do e-mail não pode ser vazio.");
        }

        @Test
        void shouldThrowEmailExceptionOnSesError() {
            SesException sesException = (SesException) SesException.builder()
                    .awsErrorDetails(
                            AwsErrorDetails.builder()
                                    .errorMessage("SES Error")
                                    .build())
                    .build();

            when(sesClient.sendEmail(any(SendEmailRequest.class)))
                    .thenThrow(sesException);

            assertThatThrownBy(() -> emailService.sendEmail(emailRequest, loggedInUser))
                    .isInstanceOf(EmailException.class)
                    .hasMessageContaining("Erro ao enviar e-mail via AWS SES: SES Error");
        }
    }

}
