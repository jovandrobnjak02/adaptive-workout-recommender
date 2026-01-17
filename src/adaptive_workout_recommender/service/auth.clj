(ns adaptive-workout-recommender.service.auth
  (:require
    [buddy.hashers :as hashers]
    [adaptive-workout-recommender.persistence.users :as users]
    [adaptive-workout-recommender.persistence.db :as db]))

(defn register!
  [ds {:keys [name email password]}]
  (cond
    (or (nil? name) (clojure.string/blank? name))
    {:ok false :error "Name is required."}

    (or (nil? email) (clojure.string/blank? email))
    {:ok false :error "Email is required."}

    (or (nil? password) (< (count password) 6))
    {:ok false :error "Password must be at least 6 characters."}

    (users/find-by-email ds email)
    {:ok false :error "That email is already registered."}

    :else
    (let [password-hash (hashers/derive password)
          user (users/create-user! ds {:name name :email email :password-hash password-hash})]
      {:ok true :user user})))

(defn login
  [ds {:keys [email password]}]
  (let [u (users/find-by-email ds email)]
    (cond
      (nil? u) {:ok false :error "Invalid email or password."}
      (nil? (:password_hash u)) {:ok false :error "This user has no password set."}
      (hashers/check password (:password_hash u))
      {:ok true :user {:id (:id u) :name (:name u) :email (:email u)}}
      :else {:ok false :error "Invalid email or password."})))
