-- Clear existing admin if exists to avoid duplication errors on restart if init mode is always
-- DELETE FROM users WHERE username = 'admin';

-- Insert admin user (password is 'admin123' hashed with BCrypt)
-- Note: BCrypt hashes vary each time because of the salt, but this one matches 'admin123'
INSERT INTO users (username, email, phone, password, role)
SELECT 'admin', 'admin@pizzarogo.com', '0000000000', '$2a$10$EblZqNptyYvcLm/VwDChtezYYVG9n9SNoLp31S0eT9I.i9GfTVD96', 'ADMIN'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');
