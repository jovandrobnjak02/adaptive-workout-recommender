(ns adaptive-workout-recommender.logic.generator-test
  (:require
   [midje.sweet :refer :all]
   [adaptive-workout-recommender.logic.generator :as gen]
   [adaptive-workout-recommender.fixtures :as fx]))

(facts "Workout generator"

  (fact "generates a workout with exercises"
    (let [workout
          (gen/generate-workout
           {:profile fx/beginner-profile
            :history {:last-sequence-index 0}
            :readiness {:stress 3 :fatigue 3 :sleep 8 :nutrition :neutral}
            :exercises fx/exercises
            :e1rm-by-exercise {}})]

      (:template workout) => keyword?
      (:sequence-index workout) => number?
      (count (:exercises workout)) => pos?
      (every? :exercise-id (:exercises workout)) => true))

  (fact "bad readiness produces beginner-level workouts"
    (let [workout
          (gen/generate-workout
           {:profile fx/intermediate-profile
            :history {:last-sequence-index 0}
            :readiness {:stress 9 :fatigue 9 :sleep 4 :nutrition :neutral}
            :exercises fx/exercises
            :e1rm-by-exercise {}})]

      (every? #(= 0 (:target-load %)) (:exercises workout))
      => true)))
