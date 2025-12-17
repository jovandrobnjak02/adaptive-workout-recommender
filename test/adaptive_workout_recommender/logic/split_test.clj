(ns adaptive-workout-recommender.logic.split-test
  (:require
   [midje.sweet :refer :all]
   [adaptive-workout-recommender.logic.split :as split]))

(facts "Split sequencing"

  (fact "upper-lower rotates correctly"
    (split/next-template :upper-lower 0)
    => {:template :lower-a :index 1}

    (split/next-template :upper-lower 1)
    => {:template :upper-b :index 2})

  (fact "sequence wraps around"
    (split/next-template :upper-lower 3)
    => {:template :upper-a :index 0}))
