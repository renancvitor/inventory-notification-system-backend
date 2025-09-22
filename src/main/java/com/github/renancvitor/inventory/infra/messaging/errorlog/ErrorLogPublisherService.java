package com.github.renancvitor.inventory.infra.messaging.errorlog;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.github.renancvitor.inventory.domain.entity.errorlog.ErrorLogEvent;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ErrorLogPublisherService {

    private final ApplicationEventPublisher publisher;

    public void publish(String errorType, String message, String stackTrace, String path) {
        publisher.publishEvent(new ErrorLogEvent(errorType, message, stackTrace, path));
    }

}
