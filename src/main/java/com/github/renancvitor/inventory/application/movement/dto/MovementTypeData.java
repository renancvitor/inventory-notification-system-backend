package com.github.renancvitor.inventory.application.movement.dto;

import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;

public record MovementTypeData(
        Integer id,
        String name) {

    public static MovementTypeData fromEnum(MovementTypeEnum movementTypeEnum) {
        return new MovementTypeData(movementTypeEnum.getId(), movementTypeEnum.getDisplayName());
    }
}
