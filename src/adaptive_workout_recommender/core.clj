(ns adaptive-workout-recommender.core
  (:gen-class)
  (:require
    [clojure.string :as str]
    [adaptive-workout-recommender.persistence.db :as db]
    [adaptive-workout-recommender.service.generate :as gen-svc]
    [adaptive-workout-recommender.persistence.exercises :as ex-repo]))

(defn- prompt
  [s]
  (print s)
  (flush)
  (read-line))

(defn- parse-int
  [s]
  (try
    (Integer/parseInt (str/trim s))
    (catch Exception _ nil)))

(defn- parse-double
  [s]
  (try
    (Double/parseDouble (str/trim s))
    (catch Exception _ nil)))

(defn- prompt-int-in-range
  [label minv maxv]
  (loop []
    (let [v (parse-int (prompt (format "%s (%d-%d): " label minv maxv)))]
      (if (and v (<= minv v maxv))
        v
        (do (println "   Please enter a valid number in range.")
            (recur))))))

(defn- prompt-double-min
  [label minv]
  (loop []
    (let [v (parse-double (prompt (format "%s (>= %.1f): " label (double minv))))]
      (if (and v (>= v minv))
        v
        (do (println "   Please enter a valid number.")
            (recur))))))

(defn- prompt-nutrition
  []
  (loop []
    (let [s (-> (prompt "Nutrition goal [deficit | neutral | bulk]: ")
                str/trim str/lower-case)]
      (case s
        "deficit" :deficit
        "neutral" :neutral
        "bulk"    :bulk
        (do (println "   Please type: deficit, neutral, or bulk.")
            (recur))))))

(defn- title-case
  [kw]
  (-> kw name (str/replace "-" " ") (str/split #"\s+")
      (->> (map #(str (str/upper-case (subs % 0 1)) (subs % 1))))
      (str/join " ")))

(defn- fmt-muscles
  [{:keys [main-muscle secondary-muscle]}]
  (str (or main-muscle "?")
       (when (and secondary-muscle (not (str/blank? secondary-muscle)))
         (str " (+" secondary-muscle ")"))))

(defn- fmt-load
  [x]
  (if (and x (pos? (double x)))
    (format "%.2f" (double x))
    nil))

(defn- hr
  []
  (println "----------------------------------------"))

(defn- print-workout
  [exercise-by-id {:keys [workout-id template sequence-index exercises]}]
  (println)
  (hr)
  (println (format "Workout #%d — %s (sequence %d)"
                   workout-id (title-case template) sequence-index))
  (hr)
  (println "Here’s today’s plan:")
  (println)

  (doseq [[i ex] (map-indexed vector exercises)]
    (let [eid (:exercise-id ex)
          info (get exercise-by-id eid)
          nm (or (:name info) (str "Exercise ID " eid))
          load-str (fmt-load (:target-load ex))]
      (println (format "%d) %s" (inc i) nm))
      (when info
        (println (format "   Muscles: %s" (fmt-muscles info))))
      (println (format "   Prescription: %d x %d%s   Rest: %ds"
                       (:sets ex)
                       (:reps ex)
                       (if load-str (str " @ " load-str) " @ (choose a weight)")
                       (:rest-seconds ex)))
      (when (nil? load-str)
        (println "   How to choose: pick a weight where the last 1–2 reps feel hard, but form stays clean."))
      (println)))

  (println "Tip: After training, choose option 2 to log what you actually did.")
  (println))

(defn- fetch-default-user+profile
  [ds]
  (let [row (first (db/query! ds
                              ["SELECT u.id AS user_id,
                                      p.experience,
                                      p.days_per_week,
                                      p.split
                               FROM users u
                               JOIN profiles p ON p.user_id = u.id
                               WHERE u.name = 'default'
                               ORDER BY p.created_at DESC
                               LIMIT 1"]))]

    (when row
      {:user-id (:user_id row)
       :profile {:profile/experience (keyword (:experience row))
                 :profile/split (keyword (:split row))
                 :profile/days-per-week (:days_per_week row)}})))

(defn- log-workout!
  [ds user-id exercise-by-id workout]
  (let [workout-id (:workout-id workout)]
    (println)
    (hr)
    (println (format "Logging Workout #%d" workout-id))
    (hr)
    (println "Enter what you actually performed. (You can log 0 reps if you skipped a set.)")

    (doseq [{:keys [exercise-id sets reps]} (:exercises workout)]
      (let [nm (or (get-in exercise-by-id [exercise-id :name])
                   (str "Exercise ID " exercise-id))]
        (println)
        (println (format "%s — %d set(s), target reps: %d" nm sets reps))
        (dotimes [s sets]
          (let [performed-reps (prompt-int-in-range (format "  Set %d reps" (inc s)) 0 50)
                performed-load (prompt-double-min (format "  Set %d load (kg)" (inc s)) 0.0)]
            (db/exec! ds
                      ["INSERT INTO logs (user_id, workout_id, exercise_id, set_number, reps, load)
                        VALUES (?, ?, ?, ?, ?, ?)"
                       user-id workout-id exercise-id (inc s) performed-reps performed-load])))))

    (println)
    (println "Saved your workout logs.")
    (println "Next time you generate a workout, the app will use this history to recommend loads.")
    true))

(defn- prompt-readiness
  []
  (println)
  (println "Quick check-in (helps adjust today's training):")
  {:stress   (prompt-int-in-range "Stress level (1 = calm, 10 = overwhelmed)" 1 10)
   :fatigue  (prompt-int-in-range "Fatigue level (1 = fresh, 10 = exhausted)" 1 10)
   :sleep    (prompt-int-in-range "Sleep last night (hours)" 1 8)
   :nutrition (prompt-nutrition)})

(defn- menu!
  [ds]
  (println)
  (println "Adaptive Workout Recommender — MVP")
  (println "========================================")

  (let [exercise-by-id (ex-repo/exercise-map ds)
        {:keys [user-id profile]} (or (fetch-default-user+profile ds)
                                      (do
                                        (println)
                                        (println "Default user/profile not found.")
                                        (println "Please seed the default user and profile, then run again.")
                                        nil))]
    (when (and user-id profile)
      (println)
      (println (format "Loaded profile: %s - split: %s"
                       (title-case (:profile/experience profile))
                       (title-case (:profile/split profile))))
      (loop [last-workout nil]
        (println)
        (println "What would you like to do?")
        (println "  1) Generate today's workout")
        (println "  2) Log the last generated workout")
        (println "  3) Exit")
        (case (str/trim (prompt "> "))
          "1" (let [readiness (prompt-readiness)
                    workout (gen-svc/generate-and-save-workout!
                              ds {:user-id user-id
                                  :profile profile
                                  :readiness readiness})]
                (print-workout exercise-by-id workout)
                (recur workout))

          "2" (do
                (if last-workout
                  (do (log-workout! ds user-id exercise-by-id last-workout)
                      (recur last-workout))
                  (do (println)
                      (println "You haven’t generated a workout yet.")
                      (println "Choose option 1 first.")
                      (recur last-workout))))

          "3" (do
                (println)
                (println "Bye")
                nil)

          (do
            (println)
            (println "Unknown option. Please choose 1, 2, or 3.")
            (recur last-workout)))))))

(defn -main
  [& _args]
  (let [ds (db/datasource)]
    (menu! ds)))
