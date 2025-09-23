CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    user_password VARCHAR(255) NOT NULL,

    person_id BIGINT NOT NULL,
    user_type_id INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    first_access BOOLEAN NOT NULL DEFAULT true,

    CONSTRAINT fk_user_person
        FOREIGN KEY (person_id)
        REFERENCES people(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_type
        FOREIGN KEY (user_type_id)
        REFERENCES user_types(id)
        ON DELETE CASCADE
);
