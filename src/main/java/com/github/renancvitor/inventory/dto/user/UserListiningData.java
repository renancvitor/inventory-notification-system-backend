package com.github.renancvitor.inventory.dto.user;

import com.github.renancvitor.inventory.domain.entity.user.User;

public record UserListiningData(
        Long id,
        String email,
        String personName,
        Integer idUserType,
        String userType,
        Boolean active) {

    public UserListiningData(User user) {
        this(
                user.getId(),
                user.getPersonName().getEmail(),
                user.getPersonName().getPersonName(),
                user.getUserType().getId(),
                user.getUserType().getUserTypeName(),
                user.getActive());
    }

}
