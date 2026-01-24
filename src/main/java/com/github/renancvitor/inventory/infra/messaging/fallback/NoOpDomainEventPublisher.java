package com.github.renancvitor.inventory.infra.messaging.fallback;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.domain.events.BusinessEvent;
import com.github.renancvitor.inventory.domain.events.DomainEventPublisher;

@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "none", matchIfMissing = true)
public class NoOpDomainEventPublisher implements DomainEventPublisher {

    @Override
    public void publish(BusinessEvent event) {
        // No operation performed
    }

}
