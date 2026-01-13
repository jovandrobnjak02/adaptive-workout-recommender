(ns adaptive-workout-recommender.logic.selection-test
  (:require [midje.sweet :refer :all]
            [adaptive-workout-recommender.logic.selection :as sel]))

(def exercises
  [{:exercise/id 1 :main-muscle :chest :difficulty :beginner}
   {:exercise/id 2 :main-muscle :chest :difficulty :advanced}
   {:exercise/id 3 :main-muscle :back  :difficulty :beginner}
   {:exercise/id 4 :main-muscle :legs  :difficulty :beginner}])

(facts "Exercise selection"

       (fact "filters by target muscles"
             (with-redefs [clojure.core/shuffle identity]
               (sel/select-exercises {:muscles [:chest] :difficulty-cap :advanced} exercises)
               => #(every? (fn [e] (= :chest (:main-muscle e))) %)))

       (fact "difficulty cap excludes harder exercises"
             (with-redefs [clojure.core/shuffle identity]
               (sel/select-exercises {:muscles [:chest] :difficulty-cap :beginner} exercises)
               => [{:exercise/id 1 :main-muscle :chest :difficulty :beginner}])))