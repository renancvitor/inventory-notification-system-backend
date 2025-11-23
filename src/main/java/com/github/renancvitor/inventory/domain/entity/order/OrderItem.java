package com.github.renancvitor.inventory.domain.entity.order;

import java.math.BigDecimal;

import com.github.renancvitor.inventory.domain.entity.movement.MovementTypeEntity;
import com.github.renancvitor.inventory.domain.entity.product.Product;

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

@Table(name = "order_items")
@Entity(name = "OrderItem")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "movement_type_id")
    private MovementTypeEntity movementType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "total_value", nullable = false)
    private BigDecimal totalValue = BigDecimal.ZERO;

    @PreUpdate
    public void preUpdate() {
        if (this.unitPrice != null && this.quantity != null) {
            this.totalValue = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.unitPrice != null && this.quantity != null) {
            this.totalValue = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    @Transient
    public BigDecimal getTotal() {
        return totalValue != null ? totalValue : unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

}
