package com.github.renancvitor.inventory.exception.types.common;

public class ValidationException extends RuntimeException {

    public ValidationException(String entityName, String fieldName, String message) {
        super(String.format("Erro de validação em %s: campo '%s' %s.", entityName, fieldName, message));
    }

}
