package com.github.renancvitor.inventory.domain.events;

public class EventTypes {

    public static final String ORDER_CREATED = "order.created";
    public static final String STOCK_BELOW_MINIMUM = "stock.below.minimum";

    private EventTypes() {
    }

    public static String from(BusinessEvent event) {

        if (event instanceof OrderCreationEvent) {
            return ORDER_CREATED;
        }

        if (event instanceof StockBelowMinimumEvent) {
            return STOCK_BELOW_MINIMUM;
        }

        throw new IllegalArgumentException(
                "Unknown event type: " + event.getClass().getSimpleName());
    }

}
