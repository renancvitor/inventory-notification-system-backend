package com.github.renancvitor.inventory.application.movement.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovementOrderRequest(
                @NotNull(message = "O produto é obrigatório!") Long productId,
                @NotNull(message = "Definir o tipo de movimentação é obrigatório") Integer movementTypeId,
                @NotNull(message = "A quantidade é obrigatória!") @Positive(message = "A quantidade deve ser um número positivo!") Integer quantity,
                @NotNull(message = "Valor do produto não pode ser nulo.") @DecimalMin(value = "0.01", message = "O preço deve ser maior do que 0.") @Digits(integer = 10, fraction = 2, message = "O preço deve ter no máximo 10 digitos e 2 casas decimais.") BigDecimal unitPrice) {

}
