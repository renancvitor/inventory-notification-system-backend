package com.github.renancvitor.inventory.domain.entity.user;

import com.github.renancvitor.inventory.domain.enums.user.PermissionEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permissions")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class PermissionEntity {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true)
    private String permissionName;

    public static PermissionEntity fromEnum(PermissionEnum permissionEnum) {
        return new PermissionEntity(permissionEnum.getId(), permissionEnum.name());
    }

}
