package com.github.renancvitor.inventory.infra.messaging.systemlog;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.entity.systemlog.SystemLog;
import com.github.renancvitor.inventory.domain.entity.systemlog.SystemLogEvent;
import com.github.renancvitor.inventory.repository.SystemLogRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SystemLogListener {

    private final SystemLogRepository systemLogRepository;

    @EventListener
    public void handleLogEvent(SystemLogEvent systemLogEvent) {
        SystemLog systemLog = new SystemLog();
        systemLog.setEventType(systemLogEvent.getEventType());
        systemLog.setDescription(systemLogEvent.getDescription());
        systemLog.setOldValue(systemLogEvent.getOldValue());
        systemLog.setNewValue(systemLogEvent.getNewValue());
        systemLogRepository.save(systemLog);
    }

}
