package com.github.renancvitor.inventory.application.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserPasswordUpdateData(
                @NotBlank(message = "A senha atual não pode estar em branco") String currentPassword,
                @NotBlank(message = "A nova senha não pode estar em branco") String newPassword,
                @NotBlank(message = "A confirmação da nova senha não pode estar em branco") String confirmNewPassword) {

}
