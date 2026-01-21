package com.github.renancvitor.inventory.domain.events;

import java.time.Instant;

public interface BusinessEvent {
    Instant ocurredAt();
}
