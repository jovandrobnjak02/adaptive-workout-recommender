(ns adaptive-workout-recommender.core)

(def upper-muscles #{:chest :back :shoulders :biceps :triceps})
(def lower-muscles #{:quads :hamstrings :glutes :calves})

(def all-muscles (set (concat upper-muscles lower-muscles)))

(defn filter-by-muscle-group
  "Return all exercises that hit at least one target muscle group."
  [exercises target-muscles]
  (filter (fn [ex] (some target-muscles (:muscles ex))) exercises))

(defn filter-by-goal
  "Keep exercises that match the training goal."
  [exercises goal]
  (filter #(= goal (:type %)) exercises))

(defn filter-by-difficulty
  "Limit exercises to those allowed by the user's experience."
  [exercises level]
  (filter (fn [ex]
            (let [d (:difficulty ex)]
              (or (= d level)
                  (= level :advanced)
                  (and (= level :intermediate)
                       (= d :beginner)))))
          exercises))

(defn select-random-exercises
  "Select n random exercises from a list."
  [exercises n]
  (take n (shuffle exercises)))


(defn muscle-frequency
  "Count number of times each muscle was trained."
  [exercises]
  (reduce (fn [freq ex]
            (reduce (fn [f m] (update f m (fnil inc 0)))
                    freq
                    (:muscles ex)))
          {}
          exercises))

(defn recommended-muscle-today
  "Pick the least-trained muscle group from history."
  [history]
  (let [freq (muscle-frequency history)
        all-mus (merge (zipmap all-muscles (repeat 0)) freq)]
    (->> all-mus (sort-by val) ffirst)))

(defn compute-intensity
  "Return :low :moderate or :high based on daily readiness."
  [{:keys [stress fatigue sleep performance experience]}]
  (let [base (case experience
               :beginner 1 :intermediate 2 :advanced 3)
        perf-score (case performance
                     :bad -1 :neutral 0 :good 1)
        score (+ base perf-score
                 (- (/ stress 2))
                 (- (/ fatigue 2))
                 (/ sleep 3))]
    (cond
      (<= score 1) :low
      (<= score 3) :moderate
      :else :high)))

(defn filter-by-intensity
  "Low → beginner only. Moderate → beginner+intermediate. High → all."
  [exercises intensity]
  (filter (fn [ex]
            (case intensity
              :low (= (:difficulty ex) :beginner)
              :moderate (#{:beginner :intermediate} (:difficulty ex))
              :high true))
          exercises))

(defn recent-muscles
  "Return muscles trained in the last N days."
  [history n]
  (let [last-days (take n (reverse history))]
    (set (mapcat :muscles (apply concat last-days)))))

(defn filter-cooldown-muscles
  "Avoid muscles trained too recently."
  [exercises cooldown-muscles]
  (filter (fn [ex]
            (empty? (clojure.set/intersection (set (:muscles ex)) cooldown-muscles)))
          exercises))

(defn choose-target-muscles
  "Automatically pick a muscle group to train today:
   - Avoid cooldown muscles
   - Prioritize undertrained muscles."
  [history cooldown-muscles]
  (let [freq (muscle-frequency (apply concat history))
        all-mus (merge (zipmap all-muscles (repeat 0)) freq)
        sorted (sort-by val all-mus)]
    ;; choose first muscle not on cooldown
    (first (remove #(cooldown-muscles (first %)) sorted))))


(defn generate-daily-workout
  "Main function:
   - Determine target muscle
   - Compute intensity
   - Filter by goal, difficulty, intensity, cooldown
   - Select 4–6 exercises"
  [exercises user history]

  (let [intensity (compute-intensity user)
        cooldown-mus (recent-muscles history 1)
        [target-muscle _] (choose-target-muscles history cooldown-mus)

        by-muscles (filter-by-muscle-group exercises #{target-muscle})
        by-goal (filter-by-goal by-muscles (:goal user))
        by-difficulty (filter-by-difficulty by-goal (:experience user))
        by-intensity (filter-by-intensity by-difficulty intensity)
        by-cooldown (filter-cooldown-muscles by-intensity cooldown-mus)
        selected (select-random-exercises by-cooldown 5)]

    {:target-muscle target-muscle
     :intensity intensity
     :exercises selected}))

(defn predict-load
  "Placeholder for ML algorithm."
  [exercise user]
  {:sets nil :weight nil})