package com.github.renancvitor.inventory.application.user.dto;

import jakarta.validation.constraints.NotNull;

public record UserTypeUpdateData(
        @NotNull Integer idUserType) {

}
