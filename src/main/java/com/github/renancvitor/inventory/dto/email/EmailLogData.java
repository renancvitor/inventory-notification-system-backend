package com.github.renancvitor.inventory.dto.email;

import com.github.renancvitor.inventory.infra.messaging.systemlog.LoggableData;

public record EmailLogData(
        String subject,
        String recipient,
        String status) implements LoggableData {

    public static EmailLogData fromEmailRequest(EmailRequest emailRequest, String status) {
        return new EmailLogData(
                emailRequest.subject(),
                emailRequest.recipient(),
                status);
    }
}
