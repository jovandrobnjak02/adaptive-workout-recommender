(ns adaptive-workout-recommender.logic.selection-test
  (:require
   [midje.sweet :refer :all]
   [adaptive-workout-recommender.logic.selection :as selection]
   [adaptive-workout-recommender.fixtures :as fx]))

(facts "Exercise selection"

  (fact "selects only target muscles"
    (let [result (selection/select-exercises
                  {:muscles [:chest]
                   :difficulty-cap :advanced}
                  fx/exercises)]
      (every? #(= :chest (:main-muscle %)) result)
      => true))

  (fact "respects difficulty cap"
    (let [result (selection/select-exercises
                  {:muscles [:shoulders]
                   :difficulty-cap :beginner}
                  fx/exercises)]
      (empty? result)
      => true)))
