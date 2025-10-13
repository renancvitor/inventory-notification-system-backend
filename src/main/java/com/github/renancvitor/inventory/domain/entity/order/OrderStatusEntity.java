package com.github.renancvitor.inventory.domain.entity.order;

import com.github.renancvitor.inventory.domain.entity.order.enums.OrderStatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class OrderStatusEntity {

    @Id
    private Integer id;

    @Column(name = "order_status_name", nullable = false, unique = true)
    private String orderStatusName;

    public static OrderStatusEntity fromEnum(OrderStatusEnum orderStatusEnum) {
        return new OrderStatusEntity(orderStatusEnum.getId(), orderStatusEnum.getDisplayName());
    }

}
