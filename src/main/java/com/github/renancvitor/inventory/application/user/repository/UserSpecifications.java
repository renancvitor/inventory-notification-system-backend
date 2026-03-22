package com.github.renancvitor.inventory.application.user.repository;

import org.springframework.data.jpa.domain.Specification;

import com.github.renancvitor.inventory.domain.entity.user.User;

public class UserSpecifications {

    public static Specification<User> active(Boolean active) {
        return (root, query, builder) -> 
            builder.equal(root.get("active"), active);
    }

    public static Specification<User> userType(String userType) {
        return (root, query, builder) -> 
            builder.equal(root.get("userType").get("userTypeName"), userType);
    }

    public static Specification<User> search(String search) {
        return (root, query, builder) -> 
            builder.like(
                builder.lower(root.get("person").get("personName")),
                "%" + search.toLowerCase() + "%"
            );
    }

}
