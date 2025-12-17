(ns adaptive-workout-recommender.logic.volume)

;;MVP rule:
;; Random + constraints
;; Later: replace with smarter logic

(defn prescription
  [exercise experience volume-multiplier]
  (let [base-sets (if (= experience :beginner) 2 3)
        sets (max 1 (Math/round (* base-sets volume-multiplier)))]
    {:sets sets
     :reps (if (= (:difficulty exercise) :beginner) 10 8)
     :rest-seconds (if (= (:difficulty exercise) :advanced) 180 120)}))
