package com.github.renancvitor.inventory.domain.entity.movement;

import com.github.renancvitor.inventory.domain.entity.movement.enums.MovementTypeEnum;

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
@Table(name = "movement_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class MovementTypeEntity {

    @Id
    private Integer id;

    @Column(name = "movement_type_name", nullable = false, unique = true)
    private String movementTypeName;

    public static MovementTypeEntity fromEnum(MovementTypeEnum movementTypeEnum) {
        return new MovementTypeEntity(movementTypeEnum.getId(), movementTypeEnum.getDisplayName());
    }

}
