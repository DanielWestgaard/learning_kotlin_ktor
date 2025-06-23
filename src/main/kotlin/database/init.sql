-- Replaces User.kt
CREATE TABLE users (
    id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL, -- should be hashed
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
);

-- Replaces the very first part of UserService
-- Inserting test users
INSERT INTO users (id, username, email, name, password) VALUES
    ('1', 'gandalf_the_grey', 'gandalf@grey.com', 'Gandalf', 'M@g1c'),
    ('2', 'gandalf_the_white', 'gandalf@white.com', 'Gandalf', 'M@g1c_');

-- Create index on username for faster lookups
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_username ON users(email);