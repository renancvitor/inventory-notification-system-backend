package com.github.renancvitor.inventory.domain.enums.movement;

public enum MovementTypeEnum {

    INPUT(1, "Entrada"),
    OUTPUT(2, "Saída");

    private final int id;
    private final String displayName;

    MovementTypeEnum(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static MovementTypeEnum fromId(int id) {
        for (MovementTypeEnum movementTypeEnum : values()) {
            if (movementTypeEnum.id == id) {
                return movementTypeEnum;
            }
        }
        throw new IllegalArgumentException("ID inválido: " + id);
    }

}
