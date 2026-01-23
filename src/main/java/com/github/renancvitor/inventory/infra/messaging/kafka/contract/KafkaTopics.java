package com.github.renancvitor.inventory.infra.messaging.kafka.contract;

import com.github.renancvitor.inventory.domain.events.BusinessEvent;
import com.github.renancvitor.inventory.domain.events.OrderCreationEvent;
import com.github.renancvitor.inventory.domain.events.StockBelowMinimumEvent;

public final class KafkaTopics {

    public static final String ORDER_CREATED_V1 = "order-created-v1";
    public static final String ORDER_CREATED_DLT = "order-created-v1-DLT";

    public static final String STOCK_BELOW_MINIMUM_V1 = "stock-below-minimum-v1";
    public static final String STOCK_BELOW_MINIMUM_DLT = "stock-below-minimum-v1-DLT";

    private KafkaTopics() {
    }

    public static String resolve(BusinessEvent event) {

        if (event instanceof OrderCreationEvent) {
            return ORDER_CREATED_V1;
        }

        if (event instanceof StockBelowMinimumEvent) {
            return STOCK_BELOW_MINIMUM_V1;
        }

        throw new IllegalArgumentException(
                "No topic mapping for event: " + event.getClass().getSimpleName());
    }
}
