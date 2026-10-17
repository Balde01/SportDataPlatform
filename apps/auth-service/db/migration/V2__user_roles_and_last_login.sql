-- Add last login timestamp to users
ALTER TABLE users
ADD COLUMN last_login_at TIMESTAMPTZ NULL;

-- Create multi-roles table
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(30) NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- Migrate existing single roles before removing the old role column
INSERT INTO user_roles (user_id, role)
SELECT id, role 
FROM users;

-- Remove obsolete single role column from users
ALTER TABLE users
DROP COLUMN role;
