-- Reset admin account
DELETE FROM users WHERE username = 'admin';

-- Credentials (USE THESE MANUALLY):
-- Email: admin@pizzarogo.com
-- Password: admin123
INSERT INTO users (username, email, phone, password, role)
VALUES ('admin', 'admin@pizzarogo.com', '0000000000', '$2a$10$3oVltvoS7vpYEgTs0Us7zeTmwqRNT5o/7SKYpcsmUSwOh5JTczefO', 'ADMIN');
