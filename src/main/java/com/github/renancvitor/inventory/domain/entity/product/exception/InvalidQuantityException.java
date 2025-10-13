package com.github.renancvitor.inventory.domain.entity.product.exception;

public class InvalidQuantityException extends RuntimeException {

    public InvalidQuantityException(String productName) {
        super("Quantidade deve ser maior do que zero para manipular o produto " + productName + ".");
    }

}
