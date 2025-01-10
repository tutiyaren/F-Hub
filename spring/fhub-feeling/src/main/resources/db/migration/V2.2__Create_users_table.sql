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
        'd3469601-e653-491f-9579-b6122dbae99b', 
        'システム', 
        '管理者', 
        'System@example.com', 
        crypt('System123', gen_salt('bf'))
    );
