package com.github.renancvitor.inventory.dto.person;

import java.time.format.DateTimeFormatter;

import com.github.renancvitor.inventory.domain.entity.person.Person;
import com.github.renancvitor.inventory.infra.messaging.systemlog.LoggableData;

public record PersonLogData(
                Long id,
                String personName,
                String cpf,
                String email,
                String registrationDate) implements LoggableData {

        public static PersonLogData fromEntity(Person person) {
                String formattedDate = person.getRegistrationDate() != null
                                ? person.getRegistrationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                : null;

                return new PersonLogData(
                                person.getId(),
                                person.getPersonName(),
                                person.getCpf(),
                                person.getEmail(),
                                formattedDate);
        }
}
