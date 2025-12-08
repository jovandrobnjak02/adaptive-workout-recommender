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

(defn calculate-muscle-imbalance
  "Given a frequency map {:chest 3 :back 1 ...}, returns a sorted list
   of muscle groups from least to most trained."
  [freq-map]
  (sort-by val freq-map))

(defn recommend-muscle-focus
  "Return the muscle group with the lowest training frequency."
  [freq-map]
  (first (first (calculate-muscle-imbalance freq-map))))

(defn filter-by-difficulty
  "Keep exercises at or below user's difficulty level."
  [exercises level]
  (filter (fn [ex]
            (let [d (:difficulty ex)]
              (or (= d level)
                  (= level :advanced)
                  (and (= level :intermediate)
                       (= d :beginner)))))
          exercises))

(defn generate-balanced-day
  "Generate a workout day that focuses on the least-trained muscle group."
  [exercises goal previous-days n]
  (let [freq (muscle-frequency previous-days)
        target (recommend-muscle-focus freq)
        goal-ex (filter-by-goal exercises goal)
        muscle-ex (filter-by-muscle-group goal-ex #{target})]
    (select-random-exercises muscle-ex n)))

(defn generate-week-plan
  "Generate a full workout plan for N days."
  [exercises goal n]
  (loop [day 1
         plan []]
    (if (> day n)
      plan
      (let [workout (generate-workout-day exercises goal #{:chest :back :legs :shoulders} 5)]
        (recur (inc day) (conj plan workout))))))