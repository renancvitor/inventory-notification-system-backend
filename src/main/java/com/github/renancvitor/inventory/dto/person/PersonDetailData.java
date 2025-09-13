package com.github.renancvitor.inventory.dto.person;

import java.time.LocalDateTime;

import com.github.renancvitor.inventory.domain.entity.person.Person;

public record PersonDetailData(String personName, String cpf, String email, LocalDateTime registrationDate) {

    public PersonDetailData(Person person) {
        this(person.getPersonName(),
                person.getCpf(),
                person.getEmail(),
                person.getRegistrationDate());
    }

}
