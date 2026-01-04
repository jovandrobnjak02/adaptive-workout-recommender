(ns adaptive-workout-recommender.persistence.db
  (:require
    [next.jdbc :as jdbc]
    [next.jdbc.result-set :as rs]))

(def default-db
  {:dbtype "postgresql"
   :dbname "adaptive_workout"
   :host "localhost"
   :port 5432
   :user "awr"
   :password "awr_pass"})

(defn datasource
  ([] (datasource default-db))
  ([db-spec] (jdbc/get-datasource db-spec)))

(defn query!
  ([ds sql-params]
   (jdbc/execute! ds sql-params {:builder-fn rs/as-unqualified-maps}))
  ([ds sql-params opts]
   (jdbc/execute! ds sql-params (merge {:builder-fn rs/as-unqualified-maps} opts))))

(defn exec!
  ([ds sql-params]
   (jdbc/execute! ds sql-params))
  ([ds sql-params opts]
   (jdbc/execute! ds sql-params opts)))
