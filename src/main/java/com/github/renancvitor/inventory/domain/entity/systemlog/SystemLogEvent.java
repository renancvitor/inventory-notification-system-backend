package com.github.renancvitor.inventory.domain.entity.systemlog;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SystemLogEvent {

    private final String eventType;
    private final String description;
    private final String oldValue;
    private final String newValue;

}
