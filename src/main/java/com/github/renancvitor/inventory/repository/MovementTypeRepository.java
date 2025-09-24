package com.github.renancvitor.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.renancvitor.inventory.domain.entity.movement.MovementTypeEntity;

public interface MovementTypeRepository extends JpaRepository<MovementTypeEntity, Integer> {
}
