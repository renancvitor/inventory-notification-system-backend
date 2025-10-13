-- Permissions
INSERT INTO permissions (id, permission_name) VALUES
(1, 'MANAGE_USERS'),
(2, 'MANAGE_PRODUCTS'),
(3, 'MOVE_PRODUCTS'),
(4, 'APPROVE_ORDER'),
(5, 'REJECT_ORDER')
ON CONFLICT (id) DO NOTHING;

-- User Types
INSERT INTO user_types (id, user_type_name) VALUES
(1, 'ADMIN'),
(2, 'PRODUCT_MANAGER'),
(3, 'COMMON')
ON CONFLICT (id) DO NOTHING;

-- Association between types and permissions
INSERT INTO user_types_permissions (user_type_id, permission_id) VALUES
-- ADMIN (ALL)
(1, 1), (1, 2), (1, 3),(1, 4), (1, 5),

-- PRODUCT_MANAGER
(2, 2), (2, 3), (2, 4), (2, 5),

-- COMMON
(3, 3)
ON CONFLICT (user_type_id, permission_id) DO NOTHING;
