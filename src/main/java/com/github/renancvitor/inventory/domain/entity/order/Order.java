package com.github.renancvitor.inventory.domain.entity.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.github.renancvitor.inventory.domain.entity.movement.Movement;
import com.github.renancvitor.inventory.domain.entity.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "orders")
@Entity(name = "Order")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "order_status_id", nullable = false)
    private OrderStatusEntity orderStatus;

    @ManyToOne
    @JoinColumn(name = "requested_by")
    private User requestedBy;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @ManyToOne
    @JoinColumn(name = "rejected_by")
    private User rejectedBy;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "movements_list", nullable = false)
    private List<Movement> movements = new ArrayList<>();

    @Column(name = "total_value", nullable = false)
    private BigDecimal totalValue = BigDecimal.ZERO;

    @Column(name = "description_text")
    private String description;

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void updateTotalValue() {
        if (movements != null && !movements.isEmpty()) {
            this.totalValue = movements.stream()
                    .map(m -> m.getTotalValue() != null ? m.getTotalValue() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            this.totalValue = BigDecimal.ZERO;
        }

        if (this.createdAt == null)
            this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

}
