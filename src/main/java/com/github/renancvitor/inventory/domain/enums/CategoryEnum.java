package com.github.renancvitor.inventory.domain.enums;

public enum CategoryEnum {
    FOOD(1, "Alimento"),
    DRINK(2, "Bebida"),
    ELECTRONICS(3, "Eletrônico"),
    CLOTHING(4, "Roupa"),
    CLEANING(5, "Limpeza"),
    COSMETICS(6, "Cosméticos"),
    OTHERS(7, "Outros");

    private final int id;
    private final String displayName;

    CategoryEnum(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static CategoryEnum fromId(int id) {
        for (CategoryEnum categoryEnum : values()) {
            if (categoryEnum.id == id) {
                return categoryEnum;
            }
        }
        throw new IllegalArgumentException("ID inválido: " + id);
    }

}
