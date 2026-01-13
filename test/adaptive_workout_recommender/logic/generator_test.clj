(ns adaptive-workout-recommender.logic.generator-test
  (:require
    [midje.sweet :refer :all]
    [adaptive-workout-recommender.logic.generator :as gen]
    [adaptive-workout-recommender.logic.split :as split]
    [adaptive-workout-recommender.logic.selection :as sel]
    [adaptive-workout-recommender.logic.volume :as vol]
    [adaptive-workout-recommender.progression.load :as load]))

(facts "Workout generator wiring"

  (with-redefs
    [split/next-template (fn [_ _] {:template :upper-a :index 0})
     sel/select-exercises (fn [_ _]
                            [{:exercise/id 1 :difficulty :beginner}])
     vol/prescription (fn [_ _ _]
                        {:sets 2 :reps 10 :rest-seconds 120})
     load/recommend-load (fn [_ _] 50.0)]

    (let [w (gen/generate-workout
              {:profile {:profile/experience :beginner
                         :profile/split :upper-lower}
               :history {:last-sequence-index 0}
               :readiness {:stress 3 :fatigue 3 :sleep 8 :nutrition :neutral}
               :exercises []
               :last-load-by-exercise {}
               :weights-by-exercise {}
               :days-since-last-by-exercise {}})]
      (:template w) => :upper-a
      (count (:exercises w)) => 1
      (-> w :exercises first :target-load) => 50.0)))
