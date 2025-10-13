package com.github.renancvitor.inventory.domain.entity.order.enums;

public enum OrderStatusEnum {

    PENDING(1, "Pendente"),
    APPROVED(2, "Aprovado"),
    REJECTED(3, "Reprovado");

    private final int id;
    private final String displayName;

    OrderStatusEnum(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static OrderStatusEnum fromId(int id) {
        for (OrderStatusEnum orderStatusEnum : values()) {
            if (orderStatusEnum.id == id) {
                return orderStatusEnum;
            }
        }
        throw new IllegalArgumentException("ID inválido: " + id);
    }

}
