package com.github.renancvitor.inventory.infra.messaging.kafka.contract;

public final class KafkaTopics {

    public static final String ORDER_CREATED_V1 = "order-created-v1";
    public static final String ORDER_CREATED_DLT = "order-created-v1-DLT";

    public static final String STOCK_BELOW_MINIMUM_V1 = "stock-below-minimum-v1";
    public static final String STOCK_BELOW_MINIMUM_DLT = "stock-below-minimum-v1-DLT";

    private KafkaTopics() {
    }
}
