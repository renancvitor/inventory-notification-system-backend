package com.github.renancvitor.inventory.application.order.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import com.github.renancvitor.inventory.domain.entity.order.Order;

public class OrderSpecifications {

    public static Specification<Order> hasStatus(Integer statusId) {
        return (root, query, builder) -> builder.equal(root.get("orderStatus").get("id"), statusId);
    }

    public static Specification<Order> search(String value) {
        return (root, query, builder) -> {
            String normalizedValue = value.trim().toLowerCase();
            if (normalizedValue.isBlank()) {
                return builder.conjunction();
            }

            if (query != null) {
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (normalizedValue.chars().allMatch(Character::isDigit)) {
                predicates.add(builder.equal(root.get("id"), Long.valueOf(normalizedValue)));
            }

            String likeValue = "%" + escapeLike(normalizedValue) + "%";

            predicates.add(builder.like(
                    builder.lower(root.join("requestedBy", JoinType.LEFT).join("person", JoinType.LEFT).get("personName")),
                    likeValue,
                    '\\'));
            predicates.add(builder.like(
                    builder.lower(root.join("approvedBy", JoinType.LEFT).join("person", JoinType.LEFT).get("personName")),
                    likeValue,
                    '\\'));
            predicates.add(builder.like(
                    builder.lower(root.join("rejectedBy", JoinType.LEFT).join("person", JoinType.LEFT).get("personName")),
                    likeValue,
                    '\\'));

            return builder.or(predicates.toArray(Predicate[]::new));
        };
    }

    public static Specification<Order> requestedBy(Long userId) {
        return (root, query, builder) -> builder.equal(root.get("requestedBy").get("id"), userId);
    }

    public static Specification<Order> approvedBy(Long userId) {
        return (root, query, builder) -> builder.equal(root.get("approvedBy").get("id"), userId);
    }

    public static Specification<Order> rejectedBy(Long userId) {
        return (root, query, builder) -> builder.equal(root.get("rejectedBy").get("id"), userId);
    }

    public static Specification<Order> createdAt(LocalDateTime date) {
        return (root, query, builder) -> builder.equal(root.get("createdAt"), date);
    }

    public static Specification<Order> updatedAt(LocalDateTime date) {
        return (root, query, builder) -> builder.equal(root.get("updatedAt"), date);
    }

    public static Specification<Order> totalValue(BigDecimal total) {
        return (root, query, builder) -> builder.equal(root.get("totalValue"), total);
    }

    private static String escapeLike(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

}
