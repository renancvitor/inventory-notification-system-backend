CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    
    category_id INTEGER NOT NULL,

    price NUMERIC(15,2) NOT NULL,
    validity DATE,
    description TEXT,
    stock INTEGER NOT NULL,
    minimum_stock INTEGER,
    brand VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,

    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id)
);