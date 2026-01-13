(ns adaptive-workout-recommender.logic.sequence-test
  (:require
    [midje.sweet :refer :all]
    [adaptive-workout-recommender.logic.sequence :as seq]))

(facts "Template to muscle mapping"

  (fact "upper templates include upper-body muscles"
    (seq/template->muscles :upper-a)
    => (contains [:chest :back :shoulders]))

  (fact "lower templates include lower-body muscles"
    (seq/template->muscles :lower-a)
    => (contains [:quads :glutes])))