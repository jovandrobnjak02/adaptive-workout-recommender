(defproject adaptive-workout-recommender "0.1.0-SNAPSHOT"
  :description "Adaptive workout recommender MVP"
  :url "https://example.com"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"}

  :dependencies
  [[org.clojure/clojure "1.12.2"]
   [com.github.seancorfield/next.jdbc "1.3.955"]
   [org.postgresql/postgresql "42.7.4"] [org.clojure/data.json "2.5.0"]]

  :plugins
  [[lein-midje "3.2.1"]]

  :test-paths ["test"]

  :repl-options
  {:init-ns adaptive-workout-recommender.core})