(ns adaptive-workout-recommender.logic.generator
  (:require
    [adaptive-workout-recommender.logic.split :as split]
    [adaptive-workout-recommender.logic.sequence :as seq]
    [adaptive-workout-recommender.logic.readiness :as readiness]
    [adaptive-workout-recommender.logic.selection :as select]
    [adaptive-workout-recommender.logic.volume :as volume]
    [adaptive-workout-recommender.progression.load :as load]))

(defn generate-workout
  [{:keys [profile history readiness exercises
           last-load-by-exercise
           weights-by-exercise
           days-since-last-by-exercise]}]

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

                 ex-id (:exercise/id ex)

                 target-load
                 (load/recommend-load
                   {:last-load (get last-load-by-exercise ex-id)
                    :model-weights (get weights-by-exercise ex-id)
                    :readiness readiness
                    :days-since-last (get days-since-last-by-exercise ex-id 999)}
                   reps)]]

       {:exercise-id ex-id
        :sets sets
        :reps reps
        :target-load target-load
        :rest-seconds rest-seconds})}))