package com.github.renancvitor.inventory.domain.entity.product;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.github.renancvitor.inventory.domain.entity.category.CategoryEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "products")
@Entity(name = "Product")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category", nullable = false)
    private CategoryEntity category;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDate validity;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private Boolean active = true;

}
