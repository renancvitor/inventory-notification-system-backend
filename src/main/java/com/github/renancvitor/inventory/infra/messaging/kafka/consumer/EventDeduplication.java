package com.github.renancvitor.inventory.infra.messaging.kafka.consumer;

public interface EventDeduplication {

    boolean isProcessed(String eventId);

    void markAsProcessed(String eventId);

}
