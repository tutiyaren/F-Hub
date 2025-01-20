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
        '61671c1f-73e6-4000-a04f-666ef023c666', 
        'システム', 
        '管理者', 
        'system@example.com', 
        crypt('System123', gen_salt('bf'))
    ),
    (
        '7dcf748b-1db6-4869-ad8c-297c219e57e6', 
        '病院', 
        '管理者', 
        'hospital@example.com', 
        crypt('Hospital123', gen_salt('bf'))
    ),
    (
        '5fc0db6e-f9d5-4f5b-8a67-56da6c7a7506', 
        'aaa', 
        'ユーザー', 
        'aaa@example.com', 
        crypt('Aaaaaaa1', gen_salt('bf'))
    ),
    (
        '5fc0db6e-f9d5-4f5b-8a67-56da6c7a7506', 
        'bbb', 
        'ユーザー', 
        'bbb@example.com', 
        crypt('Bbbbbbb2', gen_salt('bf'))
    );
