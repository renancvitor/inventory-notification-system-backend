package com.github.renancvitor.inventory.dto.person;

import java.time.LocalDateTime;

import com.github.renancvitor.inventory.domain.entity.person.Person;
import com.github.renancvitor.inventory.infra.messaging.systemlog.LoggableData;

public record PersonLogData(
        Long id,
        String personName,
        String cpf,
        String email,
        LocalDateTime registrationDate) implements LoggableData {
    public static PersonLogData fromEntity(Person person) {
        return new PersonLogData(
                person.getId(),
                person.getPersonName(),
                person.getCpf(),
                person.getEmail(),
                person.getRegistrationDate());
    }
}
