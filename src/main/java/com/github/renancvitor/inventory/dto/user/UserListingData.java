package com.github.renancvitor.inventory.dto.user;

import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;

public record UserListingData(
        Long id,
        String email,
        String personName,
        Integer idUserType,
        String userType,
        Boolean active) {

    public UserListingData(User user) {
        this(
                user.getId(),
                user.getPerson().getEmail(),
                user.getPerson().getPersonName(),
                user.getUserType().getId(),
                UserTypeEnum.valueOf(user.getUserType().getUserTypeName()).getDisplayName(),
                user.getActive());
    }

}
