package com.github.renancvitor.inventory.application.user.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.renancvitor.inventory.domain.entity.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPersonEmailAndActiveTrue(String email);

    Page<User> findAllByActiveTrue(Boolean active, Pageable pageable);

    Optional<User> findByIdAndActiveTrue(Long id);

    Optional<User> findByIdAndActiveFalse(Long id);

    Page<User> findAllByActive(Boolean active, Pageable pageable);

    Optional<User> findByIdAndFirstAccessTrue(Long id);

    @Query("""
    SELECT u FROM User u
        WHERE
            LOWER(u.person.personName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.person.email) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.userType.userTypeName) LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    Page<User> search(@Param("search") String search, Pageable pageable);

}
