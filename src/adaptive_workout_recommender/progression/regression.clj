(ns adaptive-workout-recommender.progression.regression)

(def default-weights [0 0 0 0 0 0 0])

(defn dot [a b]
  (reduce + (map * a b)))

(defn predict
  [weights features]
  (dot weights features))

(defn update-weights
  [weights features target lr]
  (let [prediction (predict weights features)
        error (- target prediction)]
    (mapv
      (fn [w x]
        (+ w (* lr error x)))
      weights
      features)))
