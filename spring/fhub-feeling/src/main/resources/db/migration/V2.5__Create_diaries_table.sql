CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE diaries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    mood_score INTEGER NOT NULL CHECK (mood_score >= 0 AND mood_score <= 99),
    good_contents VARCHAR(255) NOT NULL,
    contents VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMP NOT NULL DEFAULT current_timestamp,

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);