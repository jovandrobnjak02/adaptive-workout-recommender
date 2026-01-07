(ns adaptive-workout-recommender.progression.regression)

(def default-weights [0 0 0 0 0 0 0])

(defn predict
  [weights features]
  (reduce + (map * weights features)))

(defn update-weights
  [weights features target lr]
  (let [pred (predict weights features)
        err (- target pred)]
    (mapv + weights (mapv #(* lr err %) features))))
