package com.github.renancvitor.inventory.application.user.dto;

import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;

public record UserSummaryData(
        Long id,
        String nameUserType,
        String personName,
        String personEmail) {

    public UserSummaryData(User user) {
        this(
                user.getId(),
                UserTypeEnum.valueOf(user.getUserType().getUserTypeName()).getDisplayName(),
                user.getPerson().getPersonName(),
                user.getPerson().getEmail());
    }

}
