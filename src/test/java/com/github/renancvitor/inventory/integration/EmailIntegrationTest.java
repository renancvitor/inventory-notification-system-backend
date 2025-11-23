package com.github.renancvitor.inventory.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.renancvitor.inventory.application.email.dto.EmailRequest;
import com.github.renancvitor.inventory.application.email.dto.EmailResponse;
import com.github.renancvitor.inventory.application.email.service.EmailService;

@SpringBootTest
class EmailIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Test
    void shouldSendEmailSuccessfully() {
        EmailRequest request = new EmailRequest(
                List.of("seu-email@email.com"),
                "Teste de Notificação de Estoque",
                "<p>Este é um e-mail de teste enviado pelo SES via Spring Boot.</p>");

        EmailResponse response = emailService.sendEmail(request, null);

        System.out.println("Resultado do envio: " + response);

        assertNotNull(response);
    }

}
