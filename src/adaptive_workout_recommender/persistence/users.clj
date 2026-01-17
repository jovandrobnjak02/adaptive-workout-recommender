(ns adaptive-workout-recommender.persistence.users
  (:require [adaptive-workout-recommender.persistence.db :as db]))

(defn find-by-email
  [ds email]
  (first (db/query! ds
                    ["SELECT id, name, email, password_hash
                      FROM users
                      WHERE email = ?
                      LIMIT 1"
                     email])))

(defn create-user!
  [ds {:keys [name email password-hash]}]
  (first (db/query! ds
                    ["INSERT INTO users (name, email, password_hash)
                      VALUES (?, ?, ?)
                      RETURNING id, name, email"
                     name email password-hash])))
