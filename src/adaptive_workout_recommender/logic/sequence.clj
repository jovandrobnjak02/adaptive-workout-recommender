(ns adaptive-workout-recommender.logic.sequence)

(def template-to-muscles
  {:upper-a     [:chest :back :shoulders :arms]
   :upper-b     [:chest :back :shoulders :arms]
   :lower-a     [:quads :hamstrings :glutes :calves]
   :lower-b     [:quads :hamstrings :glutes :calves]

   :full-a      [:chest :back :legs :shoulders]
   :full-b      [:chest :back :legs]
   :full-c      [:legs :shoulders :arms]

   :push        [:chest :shoulders :triceps]
   :pull        [:back :biceps]
   :legs        [:quads :hamstrings :glutes :calves]

   :chest       [:chest]
   :back        [:back]
   :shoulders   [:shoulders]
   :arms        [:biceps :triceps]
   :legs-2      [:hamstrings :glutes :calves]})
