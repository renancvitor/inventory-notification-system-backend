package com.github.renancvitor.inventory.application.user.dto;

import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;

public record UserDetailData(
        Long id,
        String personName,
        String personCpf,
        String personEmail,
        String nameUserType,
        Boolean active) {

    public UserDetailData(User user) {
        this(
                user.getId(),
                user.getPerson().getPersonName(),
                user.getPerson().getCpf(),
                user.getPerson().getEmail(),
                UserTypeEnum.valueOf(user.getUserType().getUserTypeName()).getDisplayName(),
                user.getActive());
    }

}
