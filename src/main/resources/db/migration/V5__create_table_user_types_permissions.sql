CREATE TABLE user_types_permissions (
    user_type_id INTEGER NOT NULL,
    permission_id INTEGER NOT NULL,
    PRIMARY KEY (user_type_id, permission_id),

    CONSTRAINT fk_user_type
        FOREIGN KEY (user_type_id)
        REFERENCES user_types(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_permission
        FOREIGN KEY (permission_id)
        REFERENCES permissions(id)
        ON DELETE CASCADE
);
