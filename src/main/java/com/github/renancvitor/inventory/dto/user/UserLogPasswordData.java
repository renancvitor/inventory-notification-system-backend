package com.github.renancvitor.inventory.dto.user;

import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.infra.messaging.systemlog.LoggableData;

public record UserLogPasswordData(Long id, String maskedPassword) implements LoggableData {
    public static UserLogPasswordData fromEntity(User user) {
        return new UserLogPasswordData(
                user.getId(),
                "******");
    }
}
