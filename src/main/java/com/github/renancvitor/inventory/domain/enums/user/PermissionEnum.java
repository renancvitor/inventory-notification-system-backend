package com.github.renancvitor.inventory.domain.enums.user;

public enum PermissionEnum {

    MANAGE_USERS(1, "Gerenciar usuários"),
    MANAGE_PRODUCTS(2, "Gerenciar produtos"),
    MOVE_PRODUCTS(3, "Movimentar produtos");

    private final int id;
    private final String displayName;

    PermissionEnum(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PermissionEnum fromId(int id) {
        for (PermissionEnum permissionEnum : values()) {
            if (permissionEnum.id == id) {
                return permissionEnum;
            }
        }
        throw new IllegalArgumentException("ID inválido: " + id);
    }

}
