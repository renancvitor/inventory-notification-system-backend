package com.github.renancvitor.inventory.domain.entity.user.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException() {
        super("Você só pode alterar a sua própria senha.");
    }

}
