package com.github.renancvitor.inventory.infra.messaging.kafka.idempotency;

public interface EventDuplication {

    boolean isProcessed(String eventId);

    void markAsProcessed(String eventId);

}
