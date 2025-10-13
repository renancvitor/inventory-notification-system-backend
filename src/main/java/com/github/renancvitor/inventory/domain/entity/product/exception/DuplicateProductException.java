package com.github.renancvitor.inventory.domain.entity.product.exception;

public class DuplicateProductException extends RuntimeException {

    public DuplicateProductException(String productName) {
        super("Já existe um produto cadastrado com o nome:  " + productName);
    }

}
