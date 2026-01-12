(ns adaptive-workout-recommender.progression.load
  (:require
    [adaptive-workout-recommender.progression.features :as features]
    [adaptive-workout-recommender.progression.regression :as r]))

(defn clamp
  [x min max]
  (-> x (max min) (min max)))

(defn recommend-load
  [{:keys [last-load model-weights readiness days-since-last]} reps]
  (if (nil? last-load)
    0
    (let [weights (or model-weights r/default-weights)
          x (features/feature-vector
              (assoc readiness
                :last-load last-load
                :last-reps reps
                :days-since-last days-since-last))
          delta (r/predict weights x)
          raw (+ last-load delta)
          min-load (* last-load 0.9)
          max-load (* last-load 1.05)]
      (clamp raw min-load max-load))))
