package com.github.renancvitor.inventory.application.movement.service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

        private static final DateTimeFormatter REPORT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

        @Value("${APP_MAIL_RECIPIENT}")
        private String recipients;

        public void sendDailyReport() {
                LocalDate today = LocalDate.now();
                LocalDateTime start = today.atStartOfDay();
                LocalDateTime end = today.plusDays(1).atStartOfDay();

                List<Movement> todaysMovements = movementRepository.findByMovementationDateBetween(start, end);

                if (todaysMovements.isEmpty())
                        return;

                String html = buildHtmlReport(todaysMovements);

                List<String> emailList = Arrays.stream(recipients.split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .toList();

                if (emailList.isEmpty())
                        throw new IllegalStateException("Nenhum e-mail válido configurado como receptor do relatório.");

                String subject = "📊 Relatório diário de movimentações - " + today.format(REPORT_DATE_FORMAT);

                EmailRequest request = new EmailRequest(
                                emailList,
                                subject,
                                html);

                emailService.sendEmail(request, null);
        }

        private String buildHtmlReport(List<Movement> movements) {
                StringBuilder html = new StringBuilder();

                html.append("<h2 style='font-family: Arial, sans-serif;'>📊 Relatório diário de movimentações</h2>");

                html.append(
                                "<p style='font-family: Arial, sans-serif;'>Segue abaixo o relatório das movimentações realizadas em <strong>")
                                .append(LocalDate.now().format(REPORT_DATE_FORMAT))
                                .append("</strong>.</p>");

                html.append("<table style='border-collapse: collapse; width: 100%; font-family: Arial, sans-serif;'>");
                html.append("<thead>");
                html.append("<tr style='background-color: #f2f2f2;'>");
                html.append("<th style='border: 1px solid #ccc; padding: 8px;'>Produto</th>");
                html.append("<th style='border: 1px solid #ccc; padding: 8px;'>Tipo</th>");
                html.append("<th style='border: 1px solid #ccc; padding: 8px;'>Quantidade</th>");
                html.append("<th style='border: 1px solid #ccc; padding: 8px;'>Pedido</th>");
                html.append("<th style='border: 1px solid #ccc; padding: 8px;'>Valor</th>");
                html.append("</tr>");
                html.append("</thead>");
                html.append("<tbody>");

                for (Movement movement : movements) {

                        MovementTypeEnum typeEnum = MovementTypeEnum.fromId(movement.getMovementType().getId());

                        String qty = movement.isOutput()
                                        ? "-" + movement.getQuantity()
                                        : "+" + movement.getQuantity();

                        String typeColor = movement.isOutput() ? "red" : "green";

                        String totalFormatted = CURRENCY.format(movement.getTotal());

                        html.append("<tr>");
                        html.append("<td style='border: 1px solid #ccc; padding: 8px;'>")
                                        .append(movement.getProduct().getProductName())
                                        .append("</td>");

                        html.append("<td style='border: 1px solid #ccc; padding: 8px; color:")
                                        .append(typeColor)
                                        .append(";'>")
                                        .append(typeEnum.getDisplayName())
                                        .append("</td>");

                        html.append("<td style='border: 1px solid #ccc; padding: 8px;'>")
                                        .append(qty)
                                        .append("</td>");

                        html.append("<td style='border: 1px solid #ccc; padding: 8px;'>")
                                        .append(movement.getOrder() != null ? movement.getOrder().getId() : "-")
                                        .append("</td>");

                        html.append("<td style='border: 1px solid #ccc; padding: 8px;'>")
                                        .append(totalFormatted)
                                        .append("</td>");

                        html.append("</tr>");
                }

                html.append("</tbody>");
                html.append("</table>");

                html.append("<br><p style='font-family: Arial, sans-serif; font-size: 12px; color: #777;'>")
                                .append("Este é um e-mail automático. Por favor, não responda.")
                                .append("</p>");

                return html.toString();
        }

}
