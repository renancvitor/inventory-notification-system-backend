package com.github.renancvitor.inventory.infra.messaging.errorlog;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.application.errorlog.repository.ErrorLogRepository;
import com.github.renancvitor.inventory.domain.entity.errorlog.ErrorLog;
import com.github.renancvitor.inventory.domain.entity.errorlog.ErrorLogEvent;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ErrorLogListener {

    private final ErrorLogRepository errorLogRepository;

    @EventListener
    public void handleErrorEvent(ErrorLogEvent errorLogEvent) {
        ErrorLog errorLog = new ErrorLog();
        errorLog.setErrorType(errorLogEvent.getErrorType());
        errorLog.setMessage(errorLogEvent.getMessage());
        errorLog.setStackTrace(errorLogEvent.getStackTrace());
        errorLog.setPath(errorLogEvent.getPath());
        errorLogRepository.save(errorLog);
    }

}
