CREATE TABLE movements (
    id BIGSERIAL PRIMARY KEY,

    product_id BIGINT NOT NULL,

    movement_type_id INTEGER NOT NULL,

    quantity INTEGER NOT NULL,
    unit_price NUMERIC(15,2) NOT NULL,
    movementation_date TIMESTAMP NOT NULL,

    user_id BIGINT NOT NULL,

    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_movement_type FOREIGN KEY (movement_type_id) REFERENCES movement_types(id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);