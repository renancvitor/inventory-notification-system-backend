package com.github.renancvitor.inventory.dto.user;

import com.github.renancvitor.inventory.domain.entity.user.User;

public record UserDetailData(
        Long id, String personCpf, String personEmail, String nameUserType) {

    public UserDetailData(User user) {
        this(
                user.getId(),
                user.getPerson().getCpf(),
                user.getPerson().getEmail(),
                user.getUserType().getUserTypeName());
    }

}
