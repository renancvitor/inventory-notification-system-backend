package com.github.renancvitor.inventory.application.product.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.renancvitor.inventory.application.email.dto.EmailRequest;
import com.github.renancvitor.inventory.application.email.service.EmailService;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockMonitorService {

    private final EmailService emailService;

    @Value("${app.mail.recipient}")
    private String recipients;

    public void handleLowStock(Product product, User loggedInUser) {
        List<String> emailList = Arrays.stream(recipients.split(","))
                .map(String::trim)
                .toList();

        if (emailList.isEmpty()) {
            throw new IllegalStateException("Nenhum destinatário válido configurado para receber avisos de estoque.");
        }

        if (product.isStockLow()) {
            String body = String.format("""
                    <p>O produto <strong>%s</strong> está com apenas %d unidades em estoque.</p>

                    <table border="1" cellpadding="6" cellspacing="0" style="border-collapse: collapse;">
                        <tr>
                            <th>Produto</th>
                            <th>Estoque Atual</th>
                            <th>Estoque Mínimo</th>
                        </tr>
                        <tr>
                            <td>%s</td>
                            <td>%d</td>
                            <td>%d</td>
                        </tr>
                    </table>
                    """,
                    product.getProductName(),
                    product.getStock(),
                    product.getProductName(),
                    product.getStock(),
                    product.getMinimumStock());

            EmailRequest request = new EmailRequest(
                    emailList,
                    "⚠️ Estoque baixo: " + product.getProductName(),
                    body);

            emailService.sendEmail(request, loggedInUser);
        }
    }

}
