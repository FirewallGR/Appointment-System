-- Создание таблицы roles
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Создание таблицы users
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    second_name VARCHAR(255) NOT NULL,
    third_name VARCHAR(255),
);

-- Создание таблицы users_roles для связи Many-to-Many
CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE schedule (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    date DATE NOT NULL,
    doctor_id UUID NOT NULL,
    slots JSONB NOT NULL,
    CONSTRAINT fk_doctor FOREIGN KEY (doctor_id) REFERENCES users (id)
);
