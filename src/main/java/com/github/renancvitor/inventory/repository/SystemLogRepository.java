package com.github.renancvitor.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.renancvitor.inventory.domain.entity.systemlog.SystemLog;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
}
