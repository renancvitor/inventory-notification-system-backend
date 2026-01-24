CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id BIGINT NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_version VARCHAR(10) NOT NULL,
    payload JSONB NOT NULL,
    event_status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    sent_at TIMESTAMP
);

CREATE INDEX idx_outbox_status ON outbox_events(event_status);
CREATE INDEX idx_outbox_created_at ON outbox_events(created_at);