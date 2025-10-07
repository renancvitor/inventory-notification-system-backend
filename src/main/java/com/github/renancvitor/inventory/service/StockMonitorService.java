package com.github.renancvitor.inventory.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.dto.email.EmailRequest;

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

        if (product.isStockLow()) {
            EmailRequest request = new EmailRequest(
                    emailList,
                    "⚠️ Estoque baixo: " + product.getProductName(),
                    "<p>O produto <strong>" + product.getProductName() + "</strong> está com apenas " +
                            product.getStock() + " unidades em estoque.</p>");

            emailService.sendEmail(request, loggedInUser);
        }
    }

}
