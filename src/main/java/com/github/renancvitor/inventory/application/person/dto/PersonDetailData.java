package com.github.renancvitor.inventory.application.person.dto;

import java.time.LocalDateTime;

import com.github.renancvitor.inventory.domain.entity.person.Person;

public record PersonDetailData(Long id, String personName, String cpf, String email, LocalDateTime registrationDate) {

    public PersonDetailData(Person person) {
        this(
                person.getId(),
                person.getPersonName(),
                person.getCpf(),
                person.getEmail(),
                person.getRegistrationDate());
    }

}
