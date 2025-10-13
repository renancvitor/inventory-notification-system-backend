INSERT INTO movement_types(id, movement_type_name) VALUES
    (1, 'INPUT'),
    (2, 'OUTPUT')
ON CONFLICT (id) DO NOTHING;