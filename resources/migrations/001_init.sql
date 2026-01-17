-- =========================================
-- Adaptive Workout Recommender - MVP schema
-- PostgreSQL
-- =========================================

SET client_min_messages TO WARNING;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

DROP TABLE IF EXISTS model_weights CASCADE;
DROP TABLE IF EXISTS logs CASCADE;
DROP TABLE IF EXISTS readiness CASCADE;
DROP TABLE IF EXISTS workout_exercises CASCADE;
DROP TABLE IF EXISTS workouts CASCADE;
DROP TABLE IF EXISTS exercises CASCADE;
DROP TABLE IF EXISTS profiles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- USERS
CREATE TABLE IF NOT EXISTS users (
 id BIGSERIAL PRIMARY KEY,
 name TEXT NOT NULL,
 email TEXT NOT NULL,
 password_hash TEXT NOT NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- PROFILES
CREATE TABLE IF NOT EXISTS profiles (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  experience TEXT NOT NULL CHECK (experience IN ('beginner','intermediate','advanced')),
  days_per_week INT NOT NULL CHECK (days_per_week IN (3,4,6)),
  split TEXT NOT NULL CHECK (split IN ('full-body','upper-lower','ppl','bro')),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- EXERCISES (CATALOG)
CREATE TABLE IF NOT EXISTS exercises (
  id BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  main_muscle TEXT NOT NULL,
  secondary_muscle TEXT,
  difficulty TEXT NOT NULL CHECK (difficulty IN ('beginner','intermediate','advanced'))
);

-- WORKOUTS (GENERATED)
CREATE TABLE IF NOT EXISTS workouts (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  template TEXT NOT NULL,
  sequence_index INT NOT NULL,
  generated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- WORKOUT_EXERCISES (PRESCRIBED)
CREATE TABLE IF NOT EXISTS workout_exercises (
  id BIGSERIAL PRIMARY KEY,
  workout_id BIGINT NOT NULL REFERENCES workouts(id) ON DELETE CASCADE,
  exercise_id BIGINT NOT NULL REFERENCES exercises(id) ON DELETE RESTRICT,
  sets INT NOT NULL,
  reps INT NOT NULL,
  target_load DOUBLE PRECISION NOT NULL,
  rest_seconds INT NOT NULL
);

-- READINESS (ONE PER WORKOUT)
CREATE TABLE IF NOT EXISTS readiness (
  workout_id BIGINT PRIMARY KEY REFERENCES workouts(id) ON DELETE CASCADE,
  stress INT NOT NULL CHECK (stress BETWEEN 1 AND 10),
  fatigue INT NOT NULL CHECK (fatigue BETWEEN 1 AND 10),
  sleep INT NOT NULL CHECK (sleep BETWEEN 1 AND 8),
  nutrition TEXT NOT NULL CHECK (nutrition IN ('deficit','neutral','bulk'))
);

-- LOGS (PERFORMED SETS)
CREATE TABLE IF NOT EXISTS logs (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  workout_id BIGINT NOT NULL REFERENCES workouts(id) ON DELETE CASCADE,
  exercise_id BIGINT NOT NULL REFERENCES exercises(id) ON DELETE RESTRICT,
  set_number INT NOT NULL,
  reps INT NOT NULL,
  load DOUBLE PRECISION NOT NULL,
  logged_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- MODEL_WEIGHTS (REGRESSION WEIGHTS PER USER+EXERCISE)
CREATE TABLE IF NOT EXISTS model_weights (
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  exercise_id BIGINT NOT NULL REFERENCES exercises(id) ON DELETE RESTRICT,
  weights JSONB NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, exercise_id)
);

-- INDEXES
CREATE INDEX IF NOT EXISTS idx_logs_user_exercise
  ON logs(user_id, exercise_id);

CREATE INDEX IF NOT EXISTS idx_workouts_user_generated
  ON workouts(user_id, generated_at);

CREATE INDEX IF NOT EXISTS idx_workout_exercises_workout
  ON workout_exercises(workout_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_exercises_name ON exercises(name);

CREATE UNIQUE INDEX IF NOT EXISTS uq_users_name ON users(name);
CREATE UNIQUE INDEX IF NOT EXISTS uq_users_email ON users(email);