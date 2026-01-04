(ns adaptive-workout-recommender.persistence.exercises
  (:require
    [adaptive-workout-recommender.persistence.db :as db]))

(defn list-exercises
  [ds]
  (db/query! ds ["SELECT id, name, main_muscle, secondary_muscle, difficulty
                  FROM exercises
                  ORDER BY id ASC"]))