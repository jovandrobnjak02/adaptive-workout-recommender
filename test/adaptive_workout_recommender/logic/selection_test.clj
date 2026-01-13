(ns adaptive-workout-recommender.logic.selection-test
  (:require
    [midje.sweet :refer :all]
    [adaptive-workout-recommender.logic.selection :as sel]))

(def exercises
  [{:exercise/id 1 :main-muscle :chest :difficulty :beginner}
   {:exercise/id 2 :main-muscle :chest :difficulty :advanced}
   {:exercise/id 3 :main-muscle :back  :difficulty :beginner}])

(facts "Exercise selection"

  (fact "filters by muscle group"
    (sel/select-exercises
      {:muscles [:chest] :difficulty-cap :advanced}
      exercises)
    => (every? #(= :chest (:main-muscle %))))

  (fact "difficulty cap excludes harder exercises"
    (sel/select-exercises
      {:muscles [:chest] :difficulty-cap :beginner}
      exercises)
    => [{:exercise/id 1 :main-muscle :chest :difficulty :beginner}]))
