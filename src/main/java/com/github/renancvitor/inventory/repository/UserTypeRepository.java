package com.github.renancvitor.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.renancvitor.inventory.domain.entity.user.UserTypeEntity;

public interface UserTypeRepository extends JpaRepository<UserTypeEntity, Integer> {

    Optional<UserTypeEntity> findByUserTypeName(String userTypeName);

}
