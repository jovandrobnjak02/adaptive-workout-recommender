(ns adaptive-workout-recommender.core)

(defn filter-by-muscle-group
  "Return all exercises that hit at least one of the target muscle groups."
  [exercises target-muscles]
  (filter (fn [ex]
            (some target-muscles (:muscles ex)))
          exercises))

(defn filter-by-goal
  "Keep exercises that match the training goal."
  [exercises goal]
  (filter (fn [ex]
            (= goal (:type ex)))
          exercises))

(defn select-random-exercises
  "Select n random exercises from a list."
  [exercises n]
  (take n (shuffle exercises)))


(defn muscle-frequency
  "Return a map of muscle-group TO frequency in a given exercise list."
  [exercises]
  (reduce (fn [freq ex]
            (reduce (fn [f m]
                      (update f m (fn [x] (if x (inc x) 1))))
                    freq
                    (:muscles ex)))
          {}
          exercises))

(defn generate-workout-day
  "Generate a workout day based on goal, target muscles, and exercises list."
  [exercises goal target-muscles n]
  (let [goal-ex (filter-by-goal exercises goal)
        muscle-ex (filter-by-muscle-group goal-ex target-muscles)]
    (select-random-exercises muscle-ex n)))