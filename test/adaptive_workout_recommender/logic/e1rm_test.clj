(ns adaptive-workout-recommender.progression.e1rm-test
  (:require
   [midje.sweet :refer :all]
   [adaptive-workout-recommender.progression.e1rm :as e1rm]))

(facts "E1RM calculations"

  (fact "Epley formula works"
    (e1rm/epley 100 5)
    => (roughly 116.6 0.1))

  (fact "target load scales with intensity"
    (e1rm/target-load 120 8 1.0)
    => (roughly 96 2)))
