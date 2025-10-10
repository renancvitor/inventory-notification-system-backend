package com.github.renancvitor.inventory.application.email.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.renancvitor.inventory.application.email.dto.EmailLogData;
import com.github.renancvitor.inventory.application.email.dto.EmailRequest;
import com.github.renancvitor.inventory.application.email.dto.EmailResponse;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.exception.types.email.EmailException;
import com.github.renancvitor.inventory.infra.messaging.systemlog.SystemLogPublisherService;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;
import software.amazon.awssdk.services.ses.model.SesException;

@Service
@RequiredArgsConstructor
public class EmailService {

        private final SesClient sesClient;
        private final SystemLogPublisherService logPublisherService;

        @Value("${app.mail.sender}")
        private String secretEmailSender;

        public EmailResponse sendEmail(EmailRequest emailRequest, User loggedInUser) {
                if (emailRequest.recipient() == null || emailRequest.recipient().isEmpty()) {
                        throw new EmailException("O destinatário do e-mail não pode ser vazio.");
                }

                SendEmailRequest request = buildSendEmailRequest(emailRequest);

                try {
                        SendEmailResponse response = sesClient.sendEmail(request);

                        if (response != null && response.messageId() != null) {
                                EmailLogData emailLogData = EmailLogData.fromEmailRequest(emailRequest,
                                                "E-mail enviado com sucesso");

                                logPublisherService.publish(
                                                "EMAIL_SENT",
                                                emailLogData.status(),
                                                null,
                                                emailLogData);

                                return new EmailResponse(true, response.messageId(), "E-mail enviado com sucesso.");
                        } else {
                                return new EmailResponse(false, null, "Falha ao enviar e-mail.");
                        }
                } catch (SesException e) {
                        throw new EmailException(
                                        "Erro ao enviar e-mail via AWS SES: " + e.awsErrorDetails().errorMessage());
                }
        }

        private SendEmailRequest buildSendEmailRequest(EmailRequest emailRequest) {
                String plainTextBody = emailRequest.htmlBody()
                                .replaceAll("<[^>]*>", "")
                                .replaceAll("&nbsp;", " ")
                                .replaceAll("&amp;", "&");

                Message message = Message.builder()
                                .subject(Content.builder()
                                                .data(emailRequest.subject())
                                                .charset("UTF-8")
                                                .build())
                                .body(Body.builder()
                                                .html(Content.builder()
                                                                .data(emailRequest.htmlBody())
                                                                .charset("UTF-8")
                                                                .build())
                                                .text(Content.builder()
                                                                .data(plainTextBody)
                                                                .charset("UTF-8")
                                                                .build())
                                                .build())
                                .build();

                return SendEmailRequest.builder()
                                .destination(Destination.builder()
                                                .toAddresses(emailRequest.recipient())
                                                .build())
                                .message(message)
                                .source(secretEmailSender)
                                .build();
        }

}
