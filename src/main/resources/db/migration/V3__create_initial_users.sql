-- Usuários iniciais para acessar a API: um cliente e um dono de restaurante.
-- Cada um é uma pessoa com o papel correspondente, um usuário de acesso e o tipo de usuário associado.
--
-- Login     Senha       Tipo
-- cliente   cliente123  customer
-- dono      dono12345   restaurant_owner

INSERT INTO person (name, email, cpf) VALUES
    ('Cliente Exemplo', 'cliente@restaurante.com', '52998224725'),
    ('Dono Exemplo', 'dono@restaurante.com', '11144477735');

INSERT INTO customer (person_id)
SELECT id FROM person WHERE cpf = '52998224725';

INSERT INTO restaurant_owner (person_id, business_email)
SELECT id, 'contato@restaurante.com' FROM person WHERE cpf = '11144477735';

INSERT INTO app_user (person_id, login, password_hash)
SELECT id, 'cliente', '$2a$10$BXQLoc/7ku0twphYtg8EGeRLvTuCpB12JCVDmaRYmDmXkEuxvmoOm'
FROM person WHERE cpf = '52998224725';

INSERT INTO app_user (person_id, login, password_hash)
SELECT id, 'dono', '$2a$10$OsFZTrUlW424B8OuSz8IZu1rsc6aMwSa0J4u5wZIhRnQt8enOtuWu'
FROM person WHERE cpf = '11144477735';

INSERT INTO app_user_type (app_user_id, user_type_id)
SELECT u.id, t.id FROM app_user u JOIN user_type t ON t.code = 'customer'
WHERE u.login = 'cliente';

INSERT INTO app_user_type (app_user_id, user_type_id)
SELECT u.id, t.id FROM app_user u JOIN user_type t ON t.code = 'restaurant_owner'
WHERE u.login = 'dono';
