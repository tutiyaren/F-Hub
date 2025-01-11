CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_id UUID NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMP NOT NULL DEFAULT current_timestamp,

    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

INSERT INTO users (role_id, first_name, last_name, email, password) VALUES
    (
        'd01ce731-480d-4b35-bb83-7680828c8da6', 
        'システム', 
        '管理者', 
        'system@example.com', 
        crypt('System123', gen_salt('bf'))
    ),
    (
        '621566a9-2d15-4776-910f-c176696ab83c', 
        '病院', 
        '管理者', 
        'hospital@example.com', 
        crypt('Hospital123', gen_salt('bf'))
    ),
    (
        'b7ca4e4a-1c00-4ef7-922c-da4d6c3a55ec', 
        'aaa', 
        'ユーザー', 
        'aaa@example.com', 
        crypt('Aaaaaaa1', gen_salt('bf'))
    ),
    (
        'b7ca4e4a-1c00-4ef7-922c-da4d6c3a55ec', 
        'bbb', 
        'ユーザー', 
        'bbb@example.com', 
        crypt('Bbbbbbb2', gen_salt('bf'))
    );
