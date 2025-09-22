package com.github.renancvitor.inventory.domain.entity.product;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.github.renancvitor.inventory.domain.entity.category.CategoryEntity;
import com.github.renancvitor.inventory.dto.product.ProductCreationData;

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
    private String productName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = true)
    private LocalDate validity;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private Boolean active = true;

    public Product(ProductCreationData data, CategoryEntity categoryEntity) {
        this.productName = data.productName();
        this.category = categoryEntity;
        this.price = data.price();
        this.validity = data.validity();
        this.description = data.description();
        this.stock = data.stock();
        this.brand = data.brand();
        this.active = true;
    }

}
