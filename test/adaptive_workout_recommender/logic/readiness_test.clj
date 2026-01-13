(ns adaptive-workout-recommender.logic.readiness-test
  (:require
    [midje.sweet :refer :all]
    [adaptive-workout-recommender.logic.readiness :as r]))

(facts "Readiness modifiers"

  (fact "good readiness gives neutral modifiers"
    (r/readiness-modifiers
      {:stress 3 :fatigue 3 :sleep 8 :nutrition :neutral})
    => (contains {:volume-multiplier 1.0
                  :intensity-multiplier 1.0}))

  (fact "poor readiness reduces volume and intensity"
    (r/readiness-modifiers
      {:stress 9 :fatigue 9 :sleep 4 :nutrition :neutral})
    => (contains {:volume-multiplier 0.7
                  :intensity-multiplier 0.9
                  :difficulty-cap :beginner}))

  (fact "nutrition affects intensity"
    (:intensity-multiplier
      (r/readiness-modifiers
        {:stress 3 :fatigue 3 :sleep 8 :nutrition :bulk}))
    => 1.05))