-- =========================================
-- Seed default user + profile
-- =========================================

INSERT INTO users (name)
VALUES ('default')
ON CONFLICT (name) DO NOTHING;

INSERT INTO profiles (user_id, experience, days_per_week, split)
SELECT
  u.id,
  'beginner',
  4,
  'upper-lower'
FROM users u
WHERE u.name = 'default'
AND NOT EXISTS (
  SELECT 1
  FROM profiles p
  WHERE p.user_id = u.id
);
