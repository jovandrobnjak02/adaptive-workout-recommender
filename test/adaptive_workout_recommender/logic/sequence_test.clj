(ns adaptive-workout-recommender.logic.sequence-test
  (:require [midje.sweet :refer :all]
            [adaptive-workout-recommender.logic.sequence :as seq]))

(facts "Template → muscle mapping"

       (fact "upper-a contains chest and back"
             (seq/template->muscles :upper-a)
             => #(and (some #{:chest} %)
                      (some #{:back} %)))

       (fact "lower-a contains quads and glutes"
             (seq/template->muscles :lower-a)
             => #(and (some #{:quads} %)
                      (some #{:glutes} %))))