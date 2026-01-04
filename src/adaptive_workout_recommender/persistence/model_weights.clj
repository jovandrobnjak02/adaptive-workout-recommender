(ns adaptive-workout-recommender.persistence.model-weights
  (:require
    [adaptive-workout-recommender.persistence.db :as db]
    [clojure.data.json :as json]))

(defn get-weights-by-exercise
  [ds user-id]
  (let [rows (db/query! ds
                        ["SELECT exercise_id, weights
                          FROM model_weights
                          WHERE user_id = ?" user-id])]
    (into {}
          (map (fn [{:keys [exercise_id weights]}]
                 [exercise_id
                  ;; weights is jsonb; next.jdbc returns PGobject; coerce to string
                  (json/read-str (str weights))])
               rows))))

(defn upsert-weights!
  [ds user-id exercise-id weights]
  (db/exec! ds
            ["INSERT INTO model_weights (user_id, exercise_id, weights, updated_at)
              VALUES (?, ?, ?::jsonb, now())
              ON CONFLICT (user_id, exercise_id)
              DO UPDATE SET weights = EXCLUDED.weights, updated_at = now()"
             user-id exercise-id (json/write-str weights)]))
