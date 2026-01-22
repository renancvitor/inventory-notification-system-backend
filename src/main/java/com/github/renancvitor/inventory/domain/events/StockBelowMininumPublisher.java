package com.github.renancvitor.inventory.domain.events;

public interface StockBelowMininumPublisher {

    void publishStockBelowMinimumEvent(StockBelowMinimumEvent event);

}
