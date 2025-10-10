package com.github.renancvitor.inventory.application.email.dto;

public record EmailResponse(Boolean success, String messageId, String message) {
}
