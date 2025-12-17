(ns adaptive-workout-recommender.progression.e1rm)

(defn epley
  [weight reps]
  (* weight (+ 1 (/ reps 30.0))))

(defn target-load
  [e1rm reps intensity-multiplier]
  (when e1rm
    (* (/ e1rm (+ 1 (/ reps 30.0)))
       intensity-multiplier)))
