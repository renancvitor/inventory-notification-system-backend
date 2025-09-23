package com.github.renancvitor.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.renancvitor.inventory.domain.entity.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPersonEmailAndActiveTrue(String email);
}
