package com.github.renancvitor.inventory.application.person.dto;

import java.time.LocalDateTime;

import com.github.renancvitor.inventory.domain.entity.person.Person;

public record PersonListingData(Long id, String personName, String cpf, String email, LocalDateTime registrationDate,
        Boolean active) {

    public PersonListingData(Person person) {
        this(
                person.getId(),
                person.getPersonName(),
                person.getCpf(),
                person.getEmail(),
                person.getRegistrationDate(),
                person.getActive());
    }

}
