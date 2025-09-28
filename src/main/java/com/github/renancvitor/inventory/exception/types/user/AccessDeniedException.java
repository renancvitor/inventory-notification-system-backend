package com.github.renancvitor.inventory.exception.types.user;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException() {
        super("Você só pode alterar a sua própria senha.");
    }

}
