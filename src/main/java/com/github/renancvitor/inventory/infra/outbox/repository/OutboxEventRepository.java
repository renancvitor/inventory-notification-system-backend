package com.github.renancvitor.inventory.infra.outbox.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.renancvitor.inventory.infra.outbox.entity.OutboxEventEntity;
import com.github.renancvitor.inventory.infra.outbox.entity.OutboxStatus;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {

    List<OutboxEventEntity> findTop50ByEventStatusOrderByCreatedAtAsc(OutboxStatus status);

}
