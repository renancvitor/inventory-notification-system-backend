package com.github.renancvitor.inventory.domain.entity.user;

import com.github.renancvitor.inventory.domain.entity.user.enums.UserTypeEnum;

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
@Table(name = "user_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserTypeEntity {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true)
    private String userTypeName;

    public static UserTypeEntity fromEnum(UserTypeEnum userTypeEnum) {
        return new UserTypeEntity(userTypeEnum.getId(), userTypeEnum.name());
    }

}
