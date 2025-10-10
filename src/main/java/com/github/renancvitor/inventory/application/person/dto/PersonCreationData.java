package com.github.renancvitor.inventory.application.person.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PersonCreationData(
                @NotBlank(message = "Nome é obrigatório") String personName,
                @NotBlank(message = "CPF é obrigatório") @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "Formato de CPF inválido (esperado: XXX.XXX.XXX-XX") String cpf,
                @NotBlank(message = "E-mail é obrigatório") @Email(message = "Formato de e-mail inválido") String email) {

}
