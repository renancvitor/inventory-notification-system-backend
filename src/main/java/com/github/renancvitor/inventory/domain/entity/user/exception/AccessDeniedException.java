package com.github.renancvitor.inventory.domain.entity.user.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

}
