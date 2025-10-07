package com.github.renancvitor.inventory.dto.product;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreationData(
                @NotBlank(message = "O nome do produto é obrigatório.") String productName,
                @NotNull(message = "Categoria é obrigatório.") Integer categoryId,
                @NotNull(message = "O preço do produto é obrigatório.") @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.") BigDecimal price,
                @Future(message = "A validade deve ser uma data futura.") LocalDate validity,
                String description,
                @NotNull(message = "O estoque é obrigatório.") Integer stock,
                Integer minimumStock,
                @NotBlank(message = "A marca é obrigatória.") String brand) {

}
