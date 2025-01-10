CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE hospital_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    hospital_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMP NOT NULL DEFAULT current_timestamp,

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_hospital FOREIGN KEY (hospital_id) REFERENCES hospitals (id)
);