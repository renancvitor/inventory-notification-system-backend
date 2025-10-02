package com.github.renancvitor.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.renancvitor.inventory.domain.entity.movement.Movement;

public interface MovementRepository extends JpaRepository<Movement, Long> {
}
