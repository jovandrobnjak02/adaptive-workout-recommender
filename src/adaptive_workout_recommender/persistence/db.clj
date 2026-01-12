(ns adaptive-workout-recommender.persistence.db
  (:require
    [next.jdbc :as jdbc]
    [next.jdbc.result-set :as rs]))

(defn- getenv
  [k default]
  (or (System/getenv k) default))

(def default-db
  {:dbtype "postgresql"
   :dbname (getenv "DB_NAME" "adaptive_workout")
   :host   (getenv "DB_HOST" "localhost")
   :port   (Integer/parseInt (getenv "DB_PORT" "5432"))
   :user   (getenv "DB_USER" "awr")
   :password (getenv "DB_PASSWORD" "awr_pass")})

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
