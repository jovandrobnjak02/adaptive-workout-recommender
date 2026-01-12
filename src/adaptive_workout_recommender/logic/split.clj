(ns adaptive-workout-recommender.logic.split)

(def split-sequences
  {:full-body   [:full-a :full-b :full-c]
   :upper-lower [:upper-a :lower-a :upper-b :lower-b]
   :ppl         [:push :pull :legs]
   :bro         [:chest :back :legs :shoulders :arms :legs-2]})

(defn next-template
  [split last-index]
  (let [sequence (get split-sequences split)
        next-idx (mod (inc last-index) (count sequence))]
    {:template (nth sequence next-idx)
     :index next-idx}))
