package com.github.renancvitor.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.renancvitor.inventory.domain.entity.errorlog.ErrorLog;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
}
