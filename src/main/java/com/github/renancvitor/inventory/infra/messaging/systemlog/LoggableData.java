package com.github.renancvitor.inventory.infra.messaging.systemlog;

public interface LoggableData {

    static LoggableData fromEntity(Object entity) {
        throw new UnsupportedOperationException("Método precisa ser implementado na classe concreta.");
    }

}
