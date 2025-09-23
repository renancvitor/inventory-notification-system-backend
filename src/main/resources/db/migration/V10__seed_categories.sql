INSERT INTO categories(id, category_name) VALUES
    (1, 'FOOD'),
    (2, 'DRINK'),
    (3, 'ELECTRONICS'),
    (4, 'CLOTHING'),
    (5, 'CLEANING'),
    (6, 'COSMETICS'),
    (7, 'OTHERS')
ON CONFLICT (id) DO NOTHING;