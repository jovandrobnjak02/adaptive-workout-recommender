(ns adaptive-workout-recommender.persistence.history
  (:require
    [adaptive-workout-recommender.persistence.db :as db]))

(defn last-performance-by-exercise
  "Returns {exercise-id {:last-load x :days-since-last n}} for a user."
  [ds user-id]
  (let [rows (db/query! ds
                        ["SELECT DISTINCT ON (exercise_id)
                            exercise_id,
                            load AS last_load,
                            logged_at
                          FROM logs
                          WHERE user_id = ?
                          ORDER BY exercise_id, logged_at DESC" user-id])]
    (into {}
          (map (fn [{:keys [exercise_id last_load logged_at]}]
                 ;; logged_at is timestamptz -> java.time.OffsetDateTime usually
                 ;; days since last: compute in service for timezone; here quick & simple:
                 [exercise_id {:last-load (double last_load)
                               :logged-at logged_at}])
               rows))))

(defn last-load-by-exercise
  [ds user-id]
  (into {} (map (fn [[eid {:keys [last-load]}]] [eid last-load])
                (last-performance-by-exercise ds user-id))))

(defn days-since-last-by-exercise
  "Compute days since last in SQL to avoid timezone headaches."
  [ds user-id]
  (let [rows (db/query! ds
                        ["SELECT DISTINCT ON (exercise_id)
                            exercise_id,
                            GREATEST(0, (CURRENT_DATE - (logged_at::date)))::int AS days_since_last
                          FROM logs
                          WHERE user_id = ?
                          ORDER BY exercise_id, logged_at DESC" user-id])]
    (into {} (map (fn [{:keys [exercise_id days_since_last]}]
                    [exercise_id days_since_last])
                  rows))))
