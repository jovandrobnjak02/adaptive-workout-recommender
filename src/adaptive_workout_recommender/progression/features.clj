(ns adaptive-workout-recommender.progression.features)

(def nutrition->scalar
  {:deficit -1.0
   :neutral  0.0
   :bulk     1.0})

(defn feature-vector
  [{:keys [last-load last-reps sleep fatigue stress nutrition days-since-last]}]
  (let [e1rm-proxy (* last-load (+ 1 (/ last-reps 30.0)))]
    [1.0
     e1rm-proxy
     sleep
     fatigue
     stress
     (nutrition->scalar nutrition)
     days-since-last]))
