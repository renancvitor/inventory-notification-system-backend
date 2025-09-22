package com.github.renancvitor.inventory.exception.factory;

import com.github.renancvitor.inventory.exception.types.common.ValidationException;

public class ValidationExceptionFactory {

    public static ValidationException category(String fieldName, String message) {
        return new ValidationException("Categoria", fieldName, message);
    }

    public static ValidationException permission(String fieldName, String message) {
        return new ValidationException("Permissão", fieldName, message);
    }

    public static ValidationException person(String fieldName, String message) {
        return new ValidationException("Pessoa", fieldName, message);
    }

    public static ValidationException user(String fieldName, String message) {
        return new ValidationException("Usuário", fieldName, message);
    }

    public static ValidationException product(String fieldName, String message) {
        return new ValidationException("Produto", fieldName, message);
    }

}
