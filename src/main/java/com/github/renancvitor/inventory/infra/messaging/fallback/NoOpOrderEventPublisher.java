package com.github.renancvitor.inventory.infra.messaging.fallback;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.events.OrderCreationEvent;
import com.github.renancvitor.inventory.domain.events.OrderEventPublisher;

@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "none", matchIfMissing = true)
public class NoOpOrderEventPublisher implements OrderEventPublisher {

    @Override
    public void publishOrderCreatedEvent(OrderCreationEvent event) {
        // No operation performed
    }

}
