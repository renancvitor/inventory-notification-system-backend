package com.github.renancvitor.inventory.domain.events;

public interface OrderEventPublisher {

    void publishOrderCreatedEvent(OrderCreationEvent event);

}
