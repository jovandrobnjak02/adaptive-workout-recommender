(defproject adaptive-workout-recommender "0.1.0-SNAPSHOT"
  :description "Adaptive workout recommender MVP"
  :url "https://example.com"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"}

  :dependencies
  [[org.clojure/clojure "1.12.2"]
   [midje/midje "1.10.9"]]

  :plugins
  [[lein-midje "3.2.1"]]

  :test-paths ["test"]

  :repl-options
  {:init-ns adaptive-workout-recommender.core})