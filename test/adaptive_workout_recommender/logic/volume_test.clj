(ns adaptive-workout-recommender.logic.volume-test
  (:require
    [midje.sweet :refer :all]
    [adaptive-workout-recommender.logic.volume :as vol]))

(facts "Volume prescription"

  (fact "beginner gets lower volume"
    (vol/prescription {:difficulty :beginner} :beginner 1.0)
    => (contains {:sets 2 :reps 10}))

  (fact "advanced exercise increases rest"
    (:rest-seconds
      (vol/prescription {:difficulty :advanced} :intermediate 1.0))
    => 180)

  (fact "volume multiplier never drops sets below 1"
    (:sets
      (vol/prescription {:difficulty :beginner} :beginner 0.1))
    => 1))
