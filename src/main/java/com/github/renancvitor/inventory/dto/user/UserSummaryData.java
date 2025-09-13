package com.github.renancvitor.inventory.dto.user;

import com.github.renancvitor.inventory.domain.entity.user.User;

public record UserSummaryData(
        Long id,
        String nameUserType,
        String personName,
        String personEmail) {

    public UserSummaryData(User user) {
        this(
                user.getId(),
                user.getUserType().getUserTypeName(),
                user.getPersonName().getPersonName(),
                user.getPersonName().getEmail());
    }

}
