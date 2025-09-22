package com.github.renancvitor.inventory.domain.entity.errorlog;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorLogEvent {

    private final String errorType;
    private final String message;
    private final String stackTrace;
    private final String path;

}
