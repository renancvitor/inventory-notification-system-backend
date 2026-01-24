package com.github.renancvitor.inventory.infra.outbox;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "messaging.provider", havingValue = "kafka")
public class OutboxScheduler {

    private final OutboxPublisherService outboxPublisherService;

    @Scheduled(fixedDelay = 5000)
    public void processOutbox() {
        outboxPublisherService.publishPendingEvent();
    }

}
