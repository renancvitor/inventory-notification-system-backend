package com.github.renancvitor.inventory.domain.entity.movement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;
import com.github.renancvitor.inventory.domain.entity.order.Order;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "movements")
@Entity(name = "Movement")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "movement_type_id", nullable = false)
    private MovementTypeEntity movementType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "total_value", nullable = false)
    private BigDecimal totalValue = BigDecimal.ZERO;

    @Column(name = "movementation_date", nullable = false)
    private LocalDateTime movementationDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @PrePersist
    public void prePersist() {
        if (this.movementationDate == null) {
            this.movementationDate = LocalDateTime.now();
        }

        if (this.unitPrice != null && this.quantity != null) {
            this.totalValue = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.totalValue = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.unitPrice != null && this.quantity != null) {
            this.totalValue = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    @Transient
    public BigDecimal getTotal() {
        return totalValue != null ? totalValue : unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Transient
    public boolean isOutput() {
        if (movementType == null)
            return false;
        MovementTypeEnum typeEnum = MovementTypeEnum.fromId(movementType.getId());
        return typeEnum == MovementTypeEnum.OUTPUT;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        updateTotalValue();
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        updateTotalValue();
    }

    private void updateTotalValue() {
        if (this.unitPrice != null && this.quantity != null) {
            this.totalValue = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.totalValue = BigDecimal.ZERO;
        }
    }

}
