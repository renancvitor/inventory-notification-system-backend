package com.github.renancvitor.inventory.application.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.renancvitor.inventory.domain.entity.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
