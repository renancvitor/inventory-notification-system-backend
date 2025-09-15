package com.github.renancvitor.inventory.dto.person;

import com.github.renancvitor.inventory.dto.user.UserCreationData;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record PersonUserCreationData(@NotNull @Valid PersonCreationData person, UserCreationData user) {
}
