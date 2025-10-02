package com.github.renancvitor.inventory.domain.enums.movement;

public enum MovementTypeEnum {

    INPUT(1, "Entrada") {
        @Override
        public int apply(int stock, int quantity) {
            return stock + quantity;
        }
    },
    OUTPUT(2, "Saída") {
        @Override
        public int apply(int stock, int quantity) {
            return stock - quantity;
        }
    };

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

    public abstract int apply(int stock, int quantity);

    public static MovementTypeEnum fromId(int id) {
        for (MovementTypeEnum movementTypeEnum : values()) {
            if (movementTypeEnum.id == id) {
                return movementTypeEnum;
            }
        }
        throw new IllegalArgumentException("ID inválido: " + id);
    }

}
