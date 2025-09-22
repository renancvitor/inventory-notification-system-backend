package com.github.renancvitor.inventory.domain.entity.category;

import com.github.renancvitor.inventory.domain.enums.category.CategoryEnum;

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
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class CategoryEntity {

    @Id
    private Integer id;

    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

    public static CategoryEntity fromEnum(CategoryEnum categoryEnum) {
        return new CategoryEntity(categoryEnum.getId(), categoryEnum.name());
    }

}
