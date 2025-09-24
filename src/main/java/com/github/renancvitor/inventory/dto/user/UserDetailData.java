package com.github.renancvitor.inventory.dto.user;

import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.enums.user.UserTypeEnum;

public record UserDetailData(
        Long id, String personCpf, String personEmail, String nameUserType) {

    public UserDetailData(User user) {
        this(
                user.getId(),
                user.getPerson().getCpf(),
                user.getPerson().getEmail(),
                UserTypeEnum.valueOf(user.getUserType().getUserTypeName()).getDisplayName());
    }

}
