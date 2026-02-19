package com.github.renancvitor.inventory.application.movement.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.renancvitor.inventory.application.movement.dto.MovementTypeData;
import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/movement-types")
@RequiredArgsConstructor
public class MovementTypeController {

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<MovementTypeData>> listMovementTypes() {
        List<MovementTypeData> movementTypes = Arrays.stream(MovementTypeEnum.values())
                .map(MovementTypeData::fromEnum)
                .toList();

        return ResponseEntity.ok(movementTypes);
    }
}
