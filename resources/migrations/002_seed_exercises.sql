-- =========================================
-- Seed exercise catalog (idempotent via uq_exercises_name)
-- =========================================

INSERT INTO exercises (name, main_muscle, secondary_muscle, difficulty) VALUES
-- CHEST
('Barbell Bench Press', 'chest', 'triceps', 'intermediate'),
('Dumbbell Bench Press', 'chest', 'triceps', 'beginner'),
('Incline Dumbbell Press', 'chest', 'shoulders', 'beginner'),
('Incline Barbell Bench Press', 'chest', 'shoulders', 'intermediate'),
('Chest Press Machine', 'chest', 'triceps', 'beginner'),
('Cable Fly', 'chest', 'shoulders', 'beginner'),
('Pec Deck', 'chest', NULL, 'beginner'),
('Push-Up', 'chest', 'triceps', 'beginner'),
('Dips (Chest Lean)', 'chest', 'triceps', 'advanced'),

-- BACK
('Pull-Up', 'back', 'biceps', 'advanced'),
('Lat Pulldown', 'back', 'biceps', 'beginner'),
('Chest Supported Row (Machine)', 'back', 'biceps', 'beginner'),
('Seated Cable Row', 'back', 'biceps', 'beginner'),
('One-Arm Dumbbell Row', 'back', 'biceps', 'beginner'),
('Barbell Row', 'back', 'biceps', 'intermediate'),
('T-Bar Row', 'back', 'biceps', 'intermediate'),
('Straight-Arm Pulldown', 'back', NULL, 'beginner'),
('Face Pull', 'back', 'shoulders', 'beginner'),

-- SHOULDERS
('Dumbbell Overhead Press', 'shoulders', 'triceps', 'beginner'),
('Barbell Overhead Press', 'shoulders', 'triceps', 'intermediate'),
('Machine Shoulder Press', 'shoulders', 'triceps', 'beginner'),
('Dumbbell Lateral Raise', 'shoulders', NULL, 'beginner'),
('Cable Lateral Raise', 'shoulders', NULL, 'beginner'),
('Rear Delt Fly (Machine)', 'shoulders', 'back', 'beginner'),
('Upright Row (Cable)', 'shoulders', 'traps', 'intermediate'),

-- BICEPS
('Dumbbell Curl', 'biceps', NULL, 'beginner'),
('Barbell Curl', 'biceps', NULL, 'beginner'),
('Incline Dumbbell Curl', 'biceps', NULL, 'intermediate'),
('Hammer Curl', 'biceps', NULL, 'beginner'),
('Cable Curl', 'biceps', NULL, 'beginner'),
('Preacher Curl (Machine)', 'biceps', NULL, 'beginner'),

-- TRICEPS
('Triceps Pushdown (Rope)', 'triceps', NULL, 'beginner'),
('Triceps Pushdown (Bar)', 'triceps', NULL, 'beginner'),
('Overhead Triceps Extension (Cable)', 'triceps', NULL, 'beginner'),
('Skull Crushers', 'triceps', NULL, 'intermediate'),
('Close-Grip Bench Press', 'triceps', 'chest', 'intermediate'),

-- QUADS / LEGS
('Back Squat', 'quads', 'glutes', 'advanced'),
('Front Squat', 'quads', 'glutes', 'advanced'),
('Goblet Squat', 'quads', 'glutes', 'beginner'),
('Leg Press', 'quads', 'glutes', 'beginner'),
('Hack Squat Machine', 'quads', 'glutes', 'intermediate'),
('Leg Extension', 'quads', NULL, 'beginner'),
('Walking Lunges', 'quads', 'glutes', 'beginner'),

-- HAMSTRINGS / GLUTES
('Romanian Deadlift', 'hamstrings', 'glutes', 'intermediate'),
('Deadlift', 'back', 'hamstrings', 'advanced'),
('Hip Thrust', 'glutes', 'hamstrings', 'beginner'),
('Glute Bridge', 'glutes', 'hamstrings', 'beginner'),
('Leg Curl (Machine)', 'hamstrings', NULL, 'beginner'),
('Good Morning', 'hamstrings', 'back', 'advanced'),

-- CALVES
('Standing Calf Raise', 'calves', NULL, 'beginner'),
('Seated Calf Raise', 'calves', NULL, 'beginner')

ON CONFLICT (name) DO UPDATE SET
  main_muscle = EXCLUDED.main_muscle,
  secondary_muscle = EXCLUDED.secondary_muscle,
  difficulty = EXCLUDED.difficulty;