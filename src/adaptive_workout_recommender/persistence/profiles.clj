(ns adaptive-workout-recommender.persistence.profiles
  (:require [adaptive-workout-recommender.persistence.db :as db]))

(defn latest-profile
  [ds user-id]
  (first (db/query! ds
                    ["SELECT experience, days_per_week, split
                      FROM profiles
                      WHERE user_id = ?
                      ORDER BY created_at DESC
                      LIMIT 1"
                     user-id])))

(defn create-profile!
  [ds {:keys [user-id experience days-per-week split]}]
  (first (db/query! ds
                    ["INSERT INTO profiles (user_id, experience, days_per_week, split)
                      VALUES (?, ?, ?, ?)
                      RETURNING experience, days_per_week, split"
                     user-id (name experience) days-per-week (name split)])))