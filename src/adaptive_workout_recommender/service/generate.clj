(ns adaptive-workout-recommender.service.generate
  (:require
    [next.jdbc :as jdbc]
    [adaptive-workout-recommender.logic.generator :as gen]
    [adaptive-workout-recommender.persistence.exercises :as ex-repo]
    [adaptive-workout-recommender.persistence.workouts :as w-repo]
    [adaptive-workout-recommender.persistence.history :as h-repo]
    [adaptive-workout-recommender.persistence.model-weights :as mw-repo]
    [adaptive-workout-recommender.persistence.db :as db]))

(defn generate-and-save-workout!
  "Loads required inputs, generates workout, saves workout+prescription, returns workout map incl workout-id."
  [ds {:keys [user-id profile readiness]}]
  (jdbc/with-transaction [tx ds]
                         (let [exercises (ex-repo/list-exercises tx)
                               last-idx (w-repo/last-sequence-index tx user-id)
                               last-loads (h-repo/last-load-by-exercise tx user-id)
                               days-since (h-repo/days-since-last-by-exercise tx user-id)
                               weights (mw-repo/get-weights-by-exercise tx user-id)

                               workout (gen/generate-workout
                                         {:profile profile
                                          :history {:last-sequence-index last-idx}
                                          :readiness readiness
                                          :exercises (map (fn [e]
                                                            {:exercise/id (:id e)
                                                             :name (:name e)
                                                             :main-muscle (keyword (:main_muscle e))
                                                             :secondary-muscle (some-> (:secondary_muscle e) keyword)
                                                             :difficulty (keyword (:difficulty e))})
                                                          exercises)
                                          :last-load-by-exercise last-loads
                                          :weights-by-exercise weights
                                          :days-since-last-by-exercise days-since})

                               workout-id (w-repo/create-workout!
                                            tx {:user-id user-id
                                                :template (:template workout)
                                                :sequence-index (:sequence-index workout)})]

                           (w-repo/insert-workout-exercises! tx workout-id (:exercises workout))

                           ;; save readiness row
                           (db/exec! tx
                                     ["INSERT INTO readiness (workout_id, stress, fatigue, sleep, nutrition)
                  VALUES (?, ?, ?, ?, ?)"
                                      workout-id (:stress readiness) (:fatigue readiness) (:sleep readiness) (name (:nutrition readiness))])

                           (assoc workout :workout-id workout-id))))
