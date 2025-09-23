CREATE TABLE error_logs (
    id BIGSERIAL PRIMARY KEY,
    error_type VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    stack_trace TEXT,
    path TEXT,
    created_at TIMESTAMP NOT NULL
);
