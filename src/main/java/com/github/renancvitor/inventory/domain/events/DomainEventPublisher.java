package com.github.renancvitor.inventory.domain.events;

public interface DomainEventPublisher {

    void publish(BusinessEvent event);

}
