package com.github.renancvitor.inventory.application.person.dto;

import com.github.renancvitor.inventory.application.user.dto.UserCreationData;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record PersonUserCreationData(@NotNull @Valid PersonCreationData person, UserCreationData user) {
}
