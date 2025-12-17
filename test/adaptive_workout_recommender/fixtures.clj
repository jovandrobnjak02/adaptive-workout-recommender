(ns adaptive-workout-recommender.fixtures)

(def beginner-profile
  {:profile/experience :beginner
   :profile/split :upper-lower})

(def intermediate-profile
  {:profile/experience :intermediate
   :profile/split :upper-lower})

(def exercises
  [{:exercise/id 1 :name "Bench" :main-muscle :chest :difficulty :beginner}
   {:exercise/id 2 :name "Incline Bench" :main-muscle :chest :difficulty :intermediate}
   {:exercise/id 3 :name "Squat" :main-muscle :quads :difficulty :intermediate}
   {:exercise/id 4 :name "Leg Press" :main-muscle :quads :difficulty :beginner}
   {:exercise/id 5 :name "Curl" :main-muscle :biceps :difficulty :beginner}
   {:exercise/id 6 :name "Row" :main-muscle :back :difficulty :intermediate}
   {:exercise/id 7 :name "OHP" :main-muscle :shoulders :difficulty :advanced}])
