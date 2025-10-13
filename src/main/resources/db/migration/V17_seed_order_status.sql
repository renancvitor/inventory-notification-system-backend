INSERT INTO order_status(id, order_status_name) VALUES
    (1, 'PENDING'),
    (2, 'APPROVED'),
    (3, 'REJECTED')
ON CONFLICT (id) DO NOTHING;
