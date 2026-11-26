--------------------------------------------------------------------------------
-- FIELDIFY BACKEND AUTO-INITIALIZATION SCHEMA
--------------------------------------------------------------------------------

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    role VARCHAR(30) NOT NULL CHECK (role IN ('PLAYER', 'MANAGER', 'ADMIN')),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    otp_code VARCHAR(10),
    last_active_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

CREATE TABLE IF NOT EXISTS sports (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    icon VARCHAR(50),
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS terrains (
    id BIGSERIAL PRIMARY KEY,
    manager_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    sport_id BIGINT REFERENCES sports(id) ON DELETE RESTRICT,
    category VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    address VARCHAR(255) NOT NULL,
    zone VARCHAR(100) NOT NULL,
    price_per_hour NUMERIC(10, 2) NOT NULL CHECK (price_per_hour >= 0),
    rating NUMERIC(3, 2) DEFAULT 0.0 CHECK (rating >= 0.0 AND rating <= 5.0),
    equipments TEXT[],
    image_urls TEXT[],
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_terrains_sport ON terrains(sport_id);
CREATE INDEX IF NOT EXISTS idx_terrains_zone ON terrains(zone);

CREATE TABLE IF NOT EXISTS reservations (
    id BIGSERIAL PRIMARY KEY,
    terrain_id BIGINT NOT NULL REFERENCES terrains(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reservation_date VARCHAR(20) NOT NULL,
    time_slot VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELLED')),
    total_price NUMERIC(10, 2) NOT NULL CHECK (total_price >= 0),
    is_paid BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_active_reservation
ON reservations (terrain_id, reservation_date, time_slot)
WHERE status NOT IN ('CANCELLED', 'REJECTED');

CREATE TABLE IF NOT EXISTS reservation_friends (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL REFERENCES reservations(id) ON DELETE CASCADE,
    friend_name_or_email VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS invitations (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL REFERENCES reservations(id) ON DELETE CASCADE,
    sender_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    recipient_email VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL REFERENCES reservations(id) ON DELETE CASCADE,
    amount NUMERIC(10, 2) NOT NULL CHECK (amount >= 0),
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),
    transaction_reference VARCHAR(255) UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS favorites (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    terrain_id BIGINT NOT NULL REFERENCES terrains(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, terrain_id)
);

-- Seed Default Sports Reference Data
INSERT INTO sports (name, icon, description) VALUES
('Football', '⚽', 'Terrains de football 5v5, 7v7 et 11v11 en gazon synthétique ou naturel'),
('Padel', '🎾', 'Pistes de padel panoramiques aux normes internationales'),
('Tennis', '🎾', 'Courts de tennis en terre battue et surface rapide'),
('Basket-ball', '🏀', 'Terrains de basket couverts et en plein air'),
('Volley-ball', '🏐', 'Terrains de beach volley et volley en salle'),
('Golf', '⛳', 'Parcours d''entraînement, putting green et driving range'),
('Piscine privée', '🏊', 'Espaces aquatiques privés et espaces lounge')
ON CONFLICT (name) DO NOTHING;
