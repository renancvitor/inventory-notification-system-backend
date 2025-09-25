package com.github.renancvitor.inventory.infra.messaging.systemlog;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.renancvitor.inventory.domain.entity.systemlog.SystemLogEvent;
import com.github.renancvitor.inventory.exception.types.common.JsonSerializationException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SystemLogPublisherService {

    private final ApplicationEventPublisher publisher;
    private final ObjectMapper mapper = new ObjectMapper();

    public void publish(String eventType, String description, LoggableData oldData, LoggableData newData) {
        String oldValue = oldData != null ? toJson(oldData) : "N/A";
        String newValue = newData != null ? toJson(newData) : "N/A";
        publisher.publishEvent(new SystemLogEvent(eventType, description, oldValue, newValue));
    }

    public String toJson(LoggableData data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException("Erro ao serializar o objeto: " + e.getMessage(), e);
        }
    }

}
