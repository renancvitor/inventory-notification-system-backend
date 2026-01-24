package com.github.renancvitor.inventory.domain.events;

import java.time.Instant;

public record DomainEventEnvelope<T>(
                String eventId,
                String eventType,
                String version,
                Instant ocurredAt,
                String source,
                String correlationId,
                T payload) {

}
