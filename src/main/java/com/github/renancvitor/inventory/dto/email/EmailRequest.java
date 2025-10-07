package com.github.renancvitor.inventory.dto.email;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record EmailRequest(
                @NotNull @Email List<String> recipient,
                @NotNull String subject,
                @NotNull String htmlBody) {
}
