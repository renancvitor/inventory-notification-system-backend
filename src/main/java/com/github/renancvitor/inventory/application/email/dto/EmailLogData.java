package com.github.renancvitor.inventory.application.email.dto;

import java.util.List;

import com.github.renancvitor.inventory.infra.messaging.systemlog.LoggableData;

public record EmailLogData(
        String subject,
        List<String> recipient,
        String status) implements LoggableData {

    public static EmailLogData fromEmailRequest(EmailRequest emailRequest, String status) {
        return new EmailLogData(
                emailRequest.subject(),
                emailRequest.recipient(),
                status);
    }
}
