(ns adaptive-workout-recommender.logic.generator
  (:require
   [adaptive-workout-recommender.logic.split :as split]
   [adaptive-workout-recommender.logic.sequence :as seq]
   [adaptive-workout-recommender.logic.readiness :as readiness]
   [adaptive-workout-recommender.logic.selection :as select]
   [adaptive-workout-recommender.logic.volume :as volume]
   [adaptive-workout-recommender.progression.e1rm :as e1rm]))

(defn generate-workout
  [{:keys [profile history readiness exercises e1rm-by-exercise]}]

  (let [{:keys [template index]}
        (split/next-template
         (:profile/split profile)
         (:last-sequence-index history))

        muscles (get seq/template->muscles template)
        modifiers (readiness/readiness-modifiers readiness)

        selected
        (select/select-exercises
         {:muscles muscles
          :difficulty-cap (:difficulty-cap modifiers)}
         exercises)]

    {:template template
     :sequence-index index
     :exercises
     (for [ex selected
           :let [{:keys [sets reps rest-seconds]}
                 (volume/prescription
                  ex
                  (:profile/experience profile)
                  (:volume-multiplier modifiers))

                 load
                 (e1rm/target-load
                  (get e1rm-by-exercise (:exercise/id ex))
                  reps
                  (:intensity-multiplier modifiers))]]

       {:exercise-id (:exercise/id ex)
        :sets sets
        :reps reps
        :target-load (or load 0)
        :rest-seconds rest-seconds})}))
