CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,

    created_date TIMESTAMP NOT NULL,

    order_status_id INTEGER NOT NULL,

    requested_by BIGINT NOT NULL,
    approved_by BIGINT,
    rejected_by BIGINT,

    total_value NUMERIC(15,2) NOT NULL,

    description_text TEXT,

    updated_date TIMESTAMP NOT NULL,

    CONSTRAINT fk_order_status FOREIGN KEY (order_status_id) REFERENCES order_statuses(id),
    CONSTRAINT fk_requested_by FOREIGN KEY (requested_by) REFERENCES users(id),
    CONSTRAINT fk_approved_by FOREIGN KEY (approved_by) REFERENCES users(id),
    CONSTRAINT fk_rejected_by FOREIGN KEY (rejected_by) REFERENCES users(id)
);
