package com.github.renancvitor.inventory.infra.messaging.systemlog;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.github.renancvitor.inventory.domain.entity.systemlog.SystemLogEvent;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SystemLogPublisherService {

    private final ApplicationEventPublisher publisher;

    public void publish(String eventType, String description, String oldValue, String newValue) {
        publisher.publishEvent(new SystemLogEvent(eventType, description, oldValue, newValue));
    }

}
