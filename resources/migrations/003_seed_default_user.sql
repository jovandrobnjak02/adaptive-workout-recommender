-- =========================================
-- Seed default user + profile
-- =========================================

INSERT INTO users (name, email, password_hash)
VALUES ('Arnold Schwarzenegger', 'goat@governor.com', crypt('goat', gen_salt('bf')))
    ON CONFLICT (email) DO NOTHING;

INSERT INTO profiles (user_id, experience, days_per_week, split)
SELECT
    u.id,
    'beginner',
    4,
    'upper-lower'
FROM users u
WHERE u.email = 'goat@governor.com'
  AND NOT EXISTS (
    SELECT 1
    FROM profiles p
    WHERE p.user_id = u.id
);
