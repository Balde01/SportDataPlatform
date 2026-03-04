-- Add last login timestamp to users
ALTER TABLE users
ADD COLUMN last_login_at TIMESTAMPTZ NULL;

-- Remove single role column (we switch to multi-role model)
ALTER TABLE users
DROP COLUMN role;

-- Create user_roles table
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(30) NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- Index for faster role lookups
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);