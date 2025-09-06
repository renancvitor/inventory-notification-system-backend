package com.github.renancvitor.inventory.infra.messaging;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.github.renancvitor.inventory.domain.entity.systemlog.SystemLogEvent;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LogPublisherService {

    private final ApplicationEventPublisher publisher;

    public void publish(String eventType, String description) {
        publisher.publishEvent(new SystemLogEvent(eventType, description));
    }

}
