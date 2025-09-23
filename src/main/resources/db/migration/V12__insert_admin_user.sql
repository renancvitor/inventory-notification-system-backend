-- Inserir Pessoa ADMIN
INSERT INTO people (person_name, cpf, email, registration_date, active)
SELECT
    'Administrador do Sistema',
    '111.111.111-11',
    'admin@sistema.com',
    NOW(),
    true
WHERE NOT EXISTS (
    SELECT 1 FROM people WHERE email = 'admin@sistema.com'
);

-- Inserir Usuário ADMIN
-- senha: 123456 (criptografada com BCrypt)
INSERT INTO users (user_password, person_id, user_type_id, active, first_access)
SELECT
  '$2a$10$Y50UaMFOxteibQEYLrwuHeehHYfcoafCopUazP12.rqB41bsolF5.',
  p.id,
  ut.id,
  true,
  false
FROM people p, user_types ut
WHERE p.email = 'admin@sistema.com'
    AND ut.user_type_name = 'ADMIN'
    AND NOT EXISTS (
        SELECT 1 FROM users WHERE person_id = p.id
    );
