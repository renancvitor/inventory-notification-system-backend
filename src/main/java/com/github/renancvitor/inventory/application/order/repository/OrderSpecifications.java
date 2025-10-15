package com.github.renancvitor.inventory.application.order.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.github.renancvitor.inventory.domain.entity.order.Order;

public class OrderSpecifications {

    public static Specification<Order> hasStatus(Integer statusId) {
        return (root, query, builder) -> builder.equal(root.get("status").get("id"), statusId);
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

}
