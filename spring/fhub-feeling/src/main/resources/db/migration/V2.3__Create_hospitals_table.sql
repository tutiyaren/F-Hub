CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE hospitals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    postal_code VARCHAR(10) NOT NULL,
    address VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMP NOT NULL DEFAULT current_timestamp
);

INSERT INTO hospitals (name, postal_code, address, phone_number) VALUES
    (
        'テスト病院',
        '0000000',
        '東京都中央区日本橋1-1○○○○○○○○○○○階',
        '0001234567'
    );
