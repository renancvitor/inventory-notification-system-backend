package com.github.renancvitor.inventory.domain.entity.product.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productName) {
        super("Quantidade insuficiente para o produto " + productName + ".");
    }

}
