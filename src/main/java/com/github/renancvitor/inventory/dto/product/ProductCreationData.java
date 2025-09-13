package com.github.renancvitor.inventory.dto.product;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreationData(
                @NotBlank(message = "O nome do produto é obrigatório.") String name,
                @NotNull(message = "Categoria é obrigatório.") Integer categoryId,
                @NotNull(message = "O preço do produto é obrigatório.") BigDecimal price,
                @Future(message = "A validade deve ser uma data futura.") LocalDate validity,
                String description,
                @NotNull(message = "O estoque é obrigatório.") @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.") Integer stock,
                @NotBlank(message = "A marca é obrigatória.") String brand) {

}
