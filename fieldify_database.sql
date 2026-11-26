--------------------------------------------------------------------------------
-- FIELDIFY (SPORTSPOT) — REVISED SECURE POSTGRESQL SCHEMA WITH UUIDs & GIST CONFLICT EXCLUSION
--------------------------------------------------------------------------------

-- Enable extensions for UUID generation and GIST range exclusions (btree_gist)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "btree_gist";

-- Drop tables in correct foreign key dependency order (if re-running)
DROP TABLE IF EXISTS favorites CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS invitations CASCADE;
DROP TABLE IF EXISTS reservation_friends CASCADE;
DROP TABLE IF EXISTS reservations CASCADE;
DROP TABLE IF EXISTS terrains CASCADE;
DROP TABLE IF EXISTS sports CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Automated updated_at trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

--------------------------------------------------------------------------------
-- 1. USERS TABLE
--------------------------------------------------------------------------------
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL, -- Argon2id or bcrypt hash
    phone VARCHAR(50) NOT NULL,
    role VARCHAR(30) NOT NULL CHECK (role IN ('PLAYER', 'MANAGER', 'ADMIN')),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    otp_hash VARCHAR(255), -- Secure hash of OTP code
    otp_expires_at TIMESTAMPTZ,
    otp_attempts INT NOT NULL DEFAULT 0,
    last_active_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

--------------------------------------------------------------------------------
-- 2. SPORTS TABLE (Reference Data)
--------------------------------------------------------------------------------
CREATE TABLE sports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    icon VARCHAR(50),
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sports_name ON sports(name);

--------------------------------------------------------------------------------
-- 3. TERRAINS (Sports Venues / Fields) TABLE
--------------------------------------------------------------------------------
CREATE TABLE terrains (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    manager_id UUID REFERENCES users(id) ON DELETE SET NULL,
    sport_id UUID REFERENCES sports(id) ON DELETE RESTRICT,
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

CREATE INDEX idx_terrains_manager ON terrains(manager_id);
CREATE INDEX idx_terrains_sport ON terrains(sport_id);
CREATE INDEX idx_terrains_zone ON terrains(zone);

CREATE TRIGGER update_terrains_updated_at
    BEFORE UPDATE ON terrains
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

--------------------------------------------------------------------------------
-- 4. RESERVATIONS TABLE (with Timestamp Range & GIST Exclusion Constraint)
--------------------------------------------------------------------------------
CREATE TABLE reservations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    terrain_id UUID NOT NULL REFERENCES terrains(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    starts_at TIMESTAMPTZ NOT NULL,
    ends_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELLED')),
    total_price NUMERIC(10, 2) NOT NULL CHECK (total_price >= 0),
    idempotency_key VARCHAR(255) UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_reservation_time CHECK (ends_at > starts_at)
);

-- PostgreSQL Exclusion Constraint using btree_gist to strictly prevent
-- overlapping active reservations (PENDING or CONFIRMED) on the same terrain.
-- Cancelled or Rejected reservations are excluded from the conflict check.
ALTER TABLE reservations
ADD CONSTRAINT exclude_overlapping_reservations
EXCLUDE USING gist (
    terrain_id WITH =,
    tstzrange(starts_at, ends_at, '[)') WITH &&
)
WHERE (status IN ('PENDING', 'CONFIRMED'));

CREATE INDEX idx_reservations_terrain ON reservations(terrain_id);
CREATE INDEX idx_reservations_user ON reservations(user_id);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_reservations_time_range ON reservations USING gist (tstzrange(starts_at, ends_at));

CREATE TRIGGER update_reservations_updated_at
    BEFORE UPDATE ON reservations
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

--------------------------------------------------------------------------------
-- 5. RESERVATION FRIENDS (Invited participants)
--------------------------------------------------------------------------------
CREATE TABLE reservation_friends (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reservation_id UUID NOT NULL REFERENCES reservations(id) ON DELETE CASCADE,
    friend_name_or_email VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_reservation_friends_res ON reservation_friends(reservation_id);

--------------------------------------------------------------------------------
-- 6. INVITATIONS TABLE
--------------------------------------------------------------------------------
CREATE TABLE invitations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reservation_id UUID NOT NULL REFERENCES reservations(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    recipient_email VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_invitations_recipient ON invitations(recipient_email);
CREATE INDEX idx_invitations_reservation ON invitations(reservation_id);

CREATE TRIGGER update_invitations_updated_at
    BEFORE UPDATE ON invitations
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

--------------------------------------------------------------------------------
-- 7. PAYMENTS TABLE (Single Source of Truth for Payments)
--------------------------------------------------------------------------------
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reservation_id UUID NOT NULL UNIQUE REFERENCES reservations(id) ON DELETE CASCADE,
    amount NUMERIC(10, 2) NOT NULL CHECK (amount >= 0),
    payment_method VARCHAR(50) NOT NULL, -- e.g. 'D17', 'CASH', 'ONLINE'
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),
    transaction_reference VARCHAR(255) UNIQUE,
    idempotency_key VARCHAR(255) UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_reservation ON payments(reservation_id);
CREATE INDEX idx_payments_status ON payments(status);

CREATE TRIGGER update_payments_updated_at
    BEFORE UPDATE ON payments
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

--------------------------------------------------------------------------------
-- 8. FAVORITES TABLE (User favorite terrains N:N)
--------------------------------------------------------------------------------
CREATE TABLE favorites (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    terrain_id UUID NOT NULL REFERENCES terrains(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, terrain_id)
);

CREATE INDEX idx_favorites_user ON favorites(user_id);
CREATE INDEX idx_favorites_terrain ON favorites(terrain_id);

--------------------------------------------------------------------------------
-- COMMENTS FOR DESIGN DOCUMENT ALIGNMENT
--------------------------------------------------------------------------------
COMMENT ON TABLE users IS 'Registered platform users with UUID primary keys and secure authentication credentials';
COMMENT ON TABLE sports IS 'Catalog of supported sports categories';
COMMENT ON TABLE terrains IS 'Sports venues and fields managed by managers';
COMMENT ON TABLE reservations IS 'Bookings with timestamp ranges protected by GIST exclusion constraints against double-booking';
COMMENT ON TABLE reservation_friends IS 'Friends invited directly by name/email to a match reservation';
COMMENT ON TABLE invitations IS 'Match invitations sent between users';
COMMENT ON TABLE payments IS 'Single source of truth for payment transactions associated with reservations';
COMMENT ON TABLE favorites IS 'User marked favorite terrains';
