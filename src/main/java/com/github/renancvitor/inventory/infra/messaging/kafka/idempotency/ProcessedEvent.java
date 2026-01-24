package com.github.renancvitor.inventory.infra.messaging.kafka.idempotency;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "processed_events")
@AllArgsConstructor
@NoArgsConstructor
public class ProcessedEvent {

    public ProcessedEvent(String eventId) {
        this.eventId = eventId;
    }

    @Id
    private String eventId;
    @Column(name = "processed_at")
    private Instant processedAt;

}
