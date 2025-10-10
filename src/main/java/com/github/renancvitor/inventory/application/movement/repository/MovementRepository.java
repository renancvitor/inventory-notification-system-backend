package com.github.renancvitor.inventory.application.movement.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.renancvitor.inventory.domain.entity.movement.Movement;

public interface MovementRepository extends JpaRepository<Movement, Long> {

    List<Movement> findByMovementationDate(LocalDateTime date);
}
