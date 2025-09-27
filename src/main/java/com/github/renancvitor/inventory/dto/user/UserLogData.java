package com.github.renancvitor.inventory.dto.user;

import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.enums.user.UserTypeEnum;
import com.github.renancvitor.inventory.infra.messaging.systemlog.LoggableData;

public record UserLogData(
        Long id,
        String password,
        String userType) implements LoggableData {
    public static UserLogData fromEntity(User user) {
        return new UserLogData(
                user.getId(),
                user.getPassword(),
                UserTypeEnum.valueOf(user.getUserType().getUserTypeName()).getDisplayName() != null
                        ? UserTypeEnum.valueOf(user.getUserType().getUserTypeName()).getDisplayName()
                        : null);
    }
}
