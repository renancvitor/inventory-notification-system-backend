package com.github.renancvitor.inventory.exception.types.product;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productName) {
        super("Quantidade insuficiente para o produto " + productName + ".");
    }

}
