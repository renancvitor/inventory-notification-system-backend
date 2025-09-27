package com.github.renancvitor.inventory.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserCreationData(
                @NotBlank(message = "CPF é obrigatório") @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}\\-?\\d{2}", message = "Formato inválido") String personCpf,
                @NotBlank(message = "Senha é obrigatório") String password,
                @NotNull(message = "Não pode ser vazio") Integer idUserType) {

}
