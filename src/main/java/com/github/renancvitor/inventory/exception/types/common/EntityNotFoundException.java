package com.github.renancvitor.inventory.exception.types.common;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName, Object id) {
        super(String.format("%s com ID %s não encontrado(a).", entityName, id));
    }

}
