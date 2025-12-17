(ns adaptive-workout-recommender.logic.readiness)

(defn readiness-modifiers
  [{:keys [stress fatigue sleep nutrition]}]
  (let [bad-day? (or (> stress 7)
                     (> fatigue 7)
                     (< sleep 5))]
    {:volume-multiplier   (if bad-day? 0.7 1.0)
     :intensity-multiplier
     (cond
       bad-day? 0.9
       (= nutrition :bulk) 1.05
       (= nutrition :deficit) 0.95
       :else 1.0)

     :difficulty-cap
     (if bad-day? :beginner :advanced)}))
