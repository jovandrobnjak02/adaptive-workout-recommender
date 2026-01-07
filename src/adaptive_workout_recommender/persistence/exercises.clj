(ns adaptive-workout-recommender.persistence.exercises
  (:require
    [adaptive-workout-recommender.persistence.db :as db]))

(defn list-exercises
  [ds]
  (db/query! ds ["SELECT id, name, main_muscle, secondary_muscle, difficulty
                  FROM exercises
                  ORDER BY id ASC"]))

(defn exercise-map
  [ds]
  (->> (db/query! ds
                  ["SELECT id, name, main_muscle, secondary_muscle
                    FROM exercises"])
       (map (fn [r]
              [(:id r)
               {:name (:name r)
                :main-muscle (:main_muscle r)
                :secondary-muscle (:secondary_muscle r)}]))
       (into {})))