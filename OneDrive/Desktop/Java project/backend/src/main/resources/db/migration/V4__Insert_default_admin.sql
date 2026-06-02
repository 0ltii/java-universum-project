-- Default admin account (password: admin123)
INSERT INTO users (username, email, password, full_name, role)
VALUES (
    'admin',
    'admin@universum.edu',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
    'System Administrator',
    'ADMIN'
) ON CONFLICT (username) DO NOTHING;
