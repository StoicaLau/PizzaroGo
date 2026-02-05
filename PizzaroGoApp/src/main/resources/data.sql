-- Credentials (USE THESE MANUALLY):
-- Email: admin@pizzarogo.com
-- Password: admin123
INSERT INTO users (username, email, phone, password, role)
SELECT 'admin', 'admin@pizzarogo.com', '0000000000', '$2a$10$3oVltvoS7vpYEgTs0Us7zeTmwqRNT5o/7SKYpcsmUSwOh5JTczefO', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin' AND email = 'admin@pizzarogo.com');

-- Employee account
-- Password:employee123
INSERT INTO users (username, email, phone, password, role)
SELECT 'employee', 'employee@pizzarogo.com', '078909090', '$2a$10$ywl38y9BNwTrYGqdwoS7YOmzz3EULZFgmDEr9e0p9YAmx5NNosmw.', 'EMPLOYEE'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'employee' AND email = 'employee@pizzarogo.com');


