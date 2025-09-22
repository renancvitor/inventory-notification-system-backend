package com.github.renancvitor.inventory.exception.types.product;

public class DuplicateProductException extends RuntimeException {

    public DuplicateProductException(String productName) {
        super("Já existe um produto cadastrado com o nome:  " + productName);
    }

}
