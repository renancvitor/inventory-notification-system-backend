package com.github.renancvitor.inventory.application.product.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductUpdateData(
        @NotNull(message = "Nome do produto não pode ser nulo.") @NotBlank(message = "Nome do produto não pode ser vazio.") String productName,

        @NotNull(message = "Categoria do produto não pode ser nulo.") Integer categoryId,

        @NotNull(message = "Valor do produto não pode ser nulo.") @DecimalMin(value = "0.01", message = "O preço deve ser maior do que 0.") @Digits(integer = 10, fraction = 2, message = "O preço deve ter no máximo 10 digitos e 2 casas decimais.") BigDecimal price,

        @FutureOrPresent(message = "A validade deve ser a partir de hoje.") LocalDate validity,
        String description,
        Integer minimumStock,

        @NotNull(message = "Marca do produto não pode ser nulo.") @NotBlank(message = "Marca do produto não pode ser vazio.") String brand) {

}
