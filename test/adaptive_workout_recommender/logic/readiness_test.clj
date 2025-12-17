(ns adaptive-workout-recommender.logic.readiness-test
  (:require
   [midje.sweet :refer :all]
   [adaptive-workout-recommender.logic.readiness :as readiness]))

(facts "Readiness modifiers"

  (fact "good day has neutral modifiers"
    (readiness/readiness-modifiers
     {:stress 3 :fatigue 3 :sleep 8 :nutrition :neutral})
    => {:volume-multiplier 1.0
        :intensity-multiplier 1.0
        :difficulty-cap :advanced})

  (fact "bad day reduces volume and intensity"
    (readiness/readiness-modifiers
     {:stress 9 :fatigue 8 :sleep 4 :nutrition :neutral})
    => (contains {:volume-multiplier 0.7
                  :intensity-multiplier 0.9
                  :difficulty-cap :beginner})))
