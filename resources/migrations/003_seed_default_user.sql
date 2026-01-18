-- =========================================
-- Seed default user + profile
-- =========================================

INSERT INTO users (name, email, password_hash)
VALUES ('Ronnie Coleman', 'goat@gmail.com', 'bcrypt+sha512$2e8e5ed457fa909ca646917e1b30bbd3$12$8a1bf849ffb7d93177dd9fe0326f57eaf84b429a31ca1f84')
    ON CONFLICT (email) DO NOTHING;

INSERT INTO profiles (user_id, experience, days_per_week, split)
SELECT
    u.id,
    'beginner',
    4,
    'upper-lower'
FROM users u
WHERE u.email = 'goat@gmail.com'
  AND NOT EXISTS (
    SELECT 1
    FROM profiles p
    WHERE p.user_id = u.id
);
