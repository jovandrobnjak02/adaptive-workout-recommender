(ns adaptive-workout-recommender.progression.load-test
  (:require
    [midje.sweet :refer :all]
    [adaptive-workout-recommender.progression.load :as load]))

(facts "Load recommendation"

  (fact "cold start returns zero load"
    (load/recommend-load
      {:last-load nil
       :model-weights nil
       :readiness {:stress 3 :fatigue 3 :sleep 8 :nutrition :neutral}
       :days-since-last 5}
      10)
    => 0)

  (fact "load increase is capped at +5%"
    (load/recommend-load
      {:last-load 100
       :model-weights [0 1000 0 0 0 0 0]
       :readiness {:stress 3 :fatigue 3 :sleep 8 :nutrition :neutral}
       :days-since-last 1}
      10)
    => 105.0))
