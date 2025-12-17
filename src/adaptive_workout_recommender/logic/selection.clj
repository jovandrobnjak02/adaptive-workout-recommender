(ns adaptive-workout-recommender.logic.selection)

(def difficulty-rank
  {:beginner 1
   :intermediate 2
   :advanced 3})

(defn allowed?
  [exercise cap]
  (<= (difficulty-rank (:difficulty exercise))
      (difficulty-rank cap)))

(defn select-exercises
  [{:keys [muscles difficulty-cap]} exercises]
  (->> exercises
       (filter #(some #{(:main-muscle %)} muscles))
       (filter #(allowed? % difficulty-cap))
       shuffle
       (take 6)))
