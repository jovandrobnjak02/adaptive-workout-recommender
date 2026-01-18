(ns adaptive-workout-recommender.core
  (:gen-class)
  (:require
    [clojure.string :as str]
    [adaptive-workout-recommender.persistence.db :as db]
    [adaptive-workout-recommender.service.generate :as gen-svc]
    [adaptive-workout-recommender.persistence.exercises :as ex-repo]
    [adaptive-workout-recommender.service.auth :as auth]
    [adaptive-workout-recommender.persistence.profiles :as p-repo]))

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

(defn- parse-double*
  [s]
  (try
    (Double/parseDouble (str/trim s))
    (catch Exception _ nil)))

(defn- prompt-nonblank
  [label]
  (loop []
    (let [v (-> (prompt label) str/trim)]
      (if (seq v)
        v
        (do (println "Please enter a value.")
            (recur))))))

(defn- prompt-password
  []
  (prompt-nonblank "Password: "))

(defn- prompt-int-in-range
  [label minv maxv]
  (loop []
    (let [v (parse-int (prompt (format "%s (%d-%d): " label minv maxv)))]
      (if (and v (<= minv v maxv))
        v
        (do (println "Please enter a valid number in range.")
            (recur))))))

(defn- prompt-double-min
  [label minv]
  (loop []
    (let [v (parse-double* (prompt (format "%s (>= %.1f): " label (double minv))))]
      (if (and v (>= v minv))
        v
        (do (println "Please enter a valid number.")
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
        (do (println "Please type: deficit, neutral, or bulk.")
            (recur))))))

(defn- hr [] (println "----------------------------------------"))
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
  (when (and x (pos? (double x)))
    (format "%.2f" (double x))))

(defn- print-workout
  [exercise-by-id {:keys [workout-id template sequence-index exercises]}]
  (println)
  (hr)
  (println (format "Workout #%d — %s (sequence %d)"
                   workout-id (title-case template) sequence-index))
  (hr)
  (println "Here’s today’s plan:\n")

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
      (when-not load-str
        (println "   How to choose: pick a weight where the last 1–2 reps feel hard, but form stays clean."))
      (println)))

  (println "After training, choose option 2 to log what you actually did.")
  (println))

(defn- prompt-readiness
  []
  (println)
  (println "Quick check-in (helps adjust today's training):")
  {:stress    (prompt-int-in-range "Stress level (1 = calm, 10 = overwhelmed)" 1 10)
   :fatigue   (prompt-int-in-range "Fatigue level (1 = fresh, 10 = exhausted)" 1 10)
   :sleep     (prompt-int-in-range "Sleep last night (hours)" 1 8)
   :nutrition (prompt-nutrition)})

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

(defn- auth-menu!
  [ds]
  (loop []
    (println "\nWelcome")
    (println "  1) Login")
    (println "  2) Register")
    (println "  3) Exit")
    (case (str/trim (prompt "> "))
      "1" (let [email (-> (prompt-nonblank "Email: ") str/lower-case)
                password (prompt-password)
                res (auth/login ds {:email email :password password})]
            (if (:ok res)
              (do (println (format "\nLogged in as %s" (get-in res [:user :name])))
                  (:user res))
              (do (println (str "\n" (:error res)))
                  (recur))))

      "2" (let [name (prompt-nonblank "Name: ")
                email (-> (prompt-nonblank "Email: ") str/lower-case)
                password (prompt-password)
                res (auth/register! ds {:name name :email email :password password})]
            (if (:ok res)
              (do (println "\nAccount created. Please login.")
                  (recur))
              (do (println (str "\n" (:error res)))
                  (recur))))

      "3" nil
      (do (println "Invalid option.")
          (recur)))))

(defn- prompt-experience
  []
  (loop []
    (println "\nChoose experience level:")
    (println "  1) Beginner")
    (println "  2) Intermediate")
    (println "  3) Advanced")
    (case (str/trim (prompt "> "))
      "1" :beginner
      "2" :intermediate
      "3" :advanced
      (do (println "Please choose 1, 2, or 3.")
          (recur)))))

(defn- prompt-days-per-week
  []
  (loop []
    (println "\nHow many days per week do you want to train?")
    (println "  1) 3 days")
    (println "  2) 4 days")
    (println "  3) 6 days")
    (case (str/trim (prompt "> "))
      "1" 3
      "2" 4
      "3" 6
      (do (println "Please choose 1, 2, or 3.")
          (recur)))))

(defn- split-from-days
  [days]
  (case days
    3 :full-body
    4 :upper-lower
    6 :needs-choice))

(defn- prompt-6day-split
  []
  (loop []
    (println "\nFor 6 days per week, choose a split:")
    (println "  1) PPL (Push / Pull / Legs)")
    (println "  2) Bro split (body-part days)")
    (case (str/trim (prompt "> "))
      "1" :ppl
      "2" :bro
      (do (println "Please choose 1 or 2.")
          (recur)))))

(defn- configure-profile!
  "Prompts the user and creates a profile row in DB. Returns the profile map in generator format."
  [ds user-id]
  (println "\nLet’s set up your training profile.")
  (let [experience (prompt-experience)
        days (prompt-days-per-week)
        split (let [s (split-from-days days)]
                (if (= s :needs-choice) (prompt-6day-split) s))
        _ (p-repo/create-profile! ds {:user-id user-id
                                      :experience experience
                                      :days-per-week days
                                      :split split})]
    (println "\nProfile saved!")
    {:profile/experience experience
     :profile/split split
     :profile/days-per-week days}))


(defn- load-or-configure-profile!
  [ds user-id]
  (if-let [p (p-repo/latest-profile ds user-id)]
    {:profile/experience (keyword (:experience p))
     :profile/split (keyword (:split p))
     :profile/days-per-week (:days_per_week p)}
    (configure-profile! ds user-id)))

(defn- menu!
  [ds user {:keys [profile/experience profile/split profile/days-per-week] :as profile}]
  (println)
  (println "Adaptive Workout Recommender — MVP")
  (println "========================================")
  (println (format "User: %s (%s)" (:name user) (:email user)))
  (println (format "Profile: %s | Split: %s | Days/week: %d"
                   (title-case experience)
                   (title-case split)
                   days-per-week))

  (let [exercise-by-id (ex-repo/exercise-map ds)]
    (loop [last-workout nil]
      (println)
      (println "What would you like to do?")
      (println "  1) Generate today's workout")
      (println "  2) Log the last generated workout")
      (println "  3) Logout")
      (println "  4) Exit")
      (case (str/trim (prompt "> "))
        "1" (let [readiness (prompt-readiness)
                  workout (gen-svc/generate-and-save-workout!
                            ds {:user-id (:id user)
                                :profile profile
                                :readiness readiness})]
              (print-workout exercise-by-id workout)
              (recur workout))

        "2" (if last-workout
              (do (log-workout! ds (:id user) exercise-by-id last-workout)
                  (recur last-workout))
              (do (println "\nYou haven’t generated a workout yet. Choose option 1 first.")
                  (recur last-workout)))

        "3" :logout
        "4" (do (println "\nBye") :exit)

        (do (println "\nUnknown option. Please choose 1, 2, 3, or 4.")
            (recur last-workout))))))

(defn -main
  [& _args]
  (let [ds (db/datasource)]
    (loop []
      (if-let [user (auth-menu! ds)]
        (let [profile (load-or-configure-profile! ds (:id user))
              result (menu! ds user profile)]
          (case result
            :logout (do (println "\nLogged out.") (recur))
            :exit nil
            (recur)))
        (println "Bye")))))