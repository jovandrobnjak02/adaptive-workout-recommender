(ns adaptive-workout-recommender.persistence.workouts
  (:require
    [adaptive-workout-recommender.persistence.db :as db]))

(defn last-sequence-index
  "Returns last used sequence_index for a user, or -1 if none."
  [ds user-id]
  (let [rows (db/query! ds
                        ["SELECT sequence_index
                          FROM workouts
                          WHERE user_id = ?
                          ORDER BY generated_at DESC
                          LIMIT 1" user-id])]
    (or (:sequence_index (first rows)) -1)))

(defn create-workout!
  "Inserts workout row and returns created workout id."
  [ds {:keys [user-id template sequence-index]}]
  (let [res (db/query! ds
                       ["INSERT INTO workouts (user_id, template, sequence_index)
                         VALUES (?, ?, ?)
                         RETURNING id"
                        user-id (name template) sequence-index])]
    (:id (first res))))

(defn insert-workout-exercises!
  "Batch insert prescribed exercises for a workout."
  [ds workout-id prescribed]
  (doseq [{:keys [exercise-id sets reps target-load rest-seconds]} prescribed]
    (db/exec! ds
              ["INSERT INTO workout_exercises
                (workout_id, exercise_id, sets, reps, target_load, rest_seconds)
                VALUES (?, ?, ?, ?, ?, ?)"
               workout-id exercise-id sets reps target-load rest-seconds])))
