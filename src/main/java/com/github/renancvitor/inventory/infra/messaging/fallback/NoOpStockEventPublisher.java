package com.github.renancvitor.inventory.infra.messaging.fallback;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.events.StockBelowMinimumEvent;
import com.github.renancvitor.inventory.domain.events.StockBelowMininumPublisher;

@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "none", matchIfMissing = true)
public class NoOpStockEventPublisher implements StockBelowMininumPublisher {

    @Override
    public void publishStockBelowMinimumEvent(StockBelowMinimumEvent event) {
        // No operation performed
    }

}
