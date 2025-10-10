package com.github.renancvitor.inventory.application.person.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.github.renancvitor.inventory.domain.entity.person.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByIdAndActiveTrue(Long id);

    Optional<Person> findByIdAndActiveFalse(Long id);

    Page<Person> findAllByActiveTrue(Pageable pageable);

    Page<Person> findAllByActive(Boolean active, Pageable pageable);

    Optional<Person> findByPersonName(String name);

}
