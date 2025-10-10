package com.github.renancvitor.inventory.application.movement.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.renancvitor.inventory.application.email.dto.EmailRequest;
import com.github.renancvitor.inventory.application.email.service.EmailService;
import com.github.renancvitor.inventory.application.movement.repository.MovementRepository;
import com.github.renancvitor.inventory.domain.entity.movement.Movement;
import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovementReportService {

    private final MovementRepository movementRepository;
    private final EmailService emailService;

    @Value("${app.mail.recipient}")
    private String recipients;

    public void sendDailyReport() {
        List<Movement> todaysMovements = movementRepository.findByMovementationDate(LocalDateTime.now());
        if (todaysMovements.isEmpty())
            return;

        String html = buildHtmlReport(todaysMovements);

        List<String> emailList = Arrays.stream(recipients.split(","))
                .map(String::trim)
                .toList();

        EmailRequest request = new EmailRequest(
                emailList,
                "📊 Relatório diário de movimentações - " + LocalDateTime.now(),
                html);

        emailService.sendEmail(request, null);
    }

    private String buildHtmlReport(List<Movement> movements) {
        StringBuilder html = new StringBuilder();

        html.append("<h3>📊 Movimentações do dia:</h3>");
        html.append("<table style='border-collapse: collapse; width: 100%;'>");
        html.append("<thead>");
        html.append("<tr style='background-color: #f2f2f2;'>");
        html.append("<th style='border: 1px solid #ccc; padding: 8px;'>Produto</th>");
        html.append("<th style='border: 1px solid #ccc; padding: 8px;'>Tipo</th>");
        html.append("<th style='border: 1px solid #ccc; padding: 8px;'>Quantidade</th>");
        html.append("<th style='border: 1px solid #ccc; padding: 8px;'>Valor</th>");
        html.append("</tr>");
        html.append("</thead>");
        html.append("<tbody>");

        for (Movement movement : movements) {
            html.append("<tr>");
            html.append("<td>").append(movement.getProduct().getProductName()).append("</td>");
            html.append("<td style='color: ")
                    .append(movement.isOutput() ? "red" : "green")
                    .append(";'>");

            MovementTypeEnum typeEnum = MovementTypeEnum.fromId(movement.getMovementType().getId());
            html.append(typeEnum.getDisplayName());

            html.append("</td>");
            html.append("<td>").append(movement.getQuantity()).append("</td>");
            html.append("<td>R$ ").append(movement.getTotal()).append("</td>");
            html.append("</tr>");
        }

        html.append("</tbody>");
        html.append("</table>");

        return html.toString();
    }

}
