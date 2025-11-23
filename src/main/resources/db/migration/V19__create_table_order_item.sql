CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    movement_type_id BIGINT NOT NULL,

    quantity INTEGER NOT NULL,
    unit_price NUMERIC(15,2) NOT NULL,
    total_value NUMERIC(15,2) NOT NULL,

    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_movement_type FOREIGN KEY (movement_type_id) REFERENCES movement_types(id) 
);