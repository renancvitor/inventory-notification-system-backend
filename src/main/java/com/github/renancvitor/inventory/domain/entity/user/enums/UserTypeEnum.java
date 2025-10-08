package com.github.renancvitor.inventory.domain.entity.user.enums;

public enum UserTypeEnum {
    ADMIN(1, "Administrador"),
    PRODUCT_MANAGER(2, "Gerenciador de produtos"),
    COMMON(3, "Comum");

    private final int id;
    private final String displayName;

    UserTypeEnum(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserTypeEnum fromId(int id) {
        for (UserTypeEnum userTypeEnum : values()) {
            if (userTypeEnum.id == id) {
                return userTypeEnum;
            }
        }
        throw new IllegalArgumentException("ID inválido: " + id);
    }

}
