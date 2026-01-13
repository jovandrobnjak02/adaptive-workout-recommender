(ns adaptive-workout-recommender.progression.regression-test
  (:require
    [midje.sweet :refer :all]
    [adaptive-workout-recommender.progression.regression :as r]))

(facts "Linear regression math"

  (fact "predict is dot product"
    (r/predict [1 2 3] [10 10 10])
    => 60)

  (fact "weight update moves toward target"
    (r/update-weights [0 0] [1 2] 10 0.1)
    => [1.0 2.0]))
