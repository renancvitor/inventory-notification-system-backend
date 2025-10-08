package com.github.renancvitor.inventory.exception.types.auth;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;

public class AuthorizationException extends RuntimeException {

    public AuthorizationException(List<UserTypeEnum> allowedTypes) {
        super(buildMessage(allowedTypes));
    }

    public AuthorizationException(Collection<Integer> allowedTypeIds) {
        this(allowedTypeIds.stream()
                .filter(Objects::nonNull)
                .map(UserTypeEnum::fromId)
                .collect(Collectors.toList()));
    }

    private static String buildMessage(List<UserTypeEnum> allowedTypes) {
        if (allowedTypes == null || allowedTypes.isEmpty()) {
            return "Acesso não autorizado.";
        }

        if (allowedTypes.size() == 1) {
            return "Apenas o " + allowedTypes.get(0).getDisplayName() + " pode realizar esta ação.";
        }

        String joined = allowedTypes.stream()
                .map(UserTypeEnum::getDisplayName)
                .collect(Collectors.joining(" e "));

        return "Apenas o " + joined + "podem cadastrar novos produtos.";
    }

}
