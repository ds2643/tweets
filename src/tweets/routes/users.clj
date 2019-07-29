(ns tweets.routes.users
  "Contains logic supporting `sign-up` and `sign-in` routes."
  (:require
   [clojure.java.jdbc :as jdbc]
   [honeysql.core :as sql]
   [honeysql.helpers :as helpers]
   [tweets.db :as db]))


(defn get-question-ids
  "Returns collection of ids associated with all existing questions."
  [db-connection]
  (let [query (sql/format {:select [:id]
                           :from   [:questions]})]
    (map :id (jdbc/query db-connection query))))

(defn get-existing-users [db-connection]
  (let [query (sql/format {:select [:email]
                           :from   [:users]})
        result (jdbc/query db-connection query)]
    (into #{} (map :email result))))

#_
(get-existing-users db/test-db)

;; TODO: move to common
(defn user-exists? [{:keys [email] :as user-info}
                    db-connection]
  (let [existing-users (get-existing-users db-connection)]
    (contains? existing-users email)))

#_
(user-exists? {:email "ds2643@columbia.edu"} db/test-db)

#_
(user-exists? {:email "peter-sagan@columbia.edu"} db/test-db)

;; TODO: encrypt password
(defn create-user [{:as user-info :keys [email password]}
                   db-connection]
  (let [table       :users
        new-user-id (db/create-random-id table db-connection)
        statement   (-> (helpers/insert-into table)
                        (helpers/values
                         [{:id       new-user-id
                           :email    email
                           :password password}])
                        sql/format)
        outcome     (jdbc/execute! db-connection statement)]
    email))

#_
(let [user-config
      {:email     "ads2643@columbia.edu"
       :password  "potato"}]
  (create-user user-config db/test-db))

;; Procedures supporting public handlers
;; TODO: add docstring
(defn sign-up [{:keys [email password] :as user-info}
               db-connection]
  (cond (not (and email password))
        (throw (Exception. "Incomplete user information supplied."))

        (user-exists? user-info db-connection)
        (throw (Exception. "User already exists!"))

        :else (create-user user-info db-connection)))


(defn sign-in
  [{:keys [email password] :as user-info} db-connection]
  ;; Use `cond` as above to address incomplete user-info case
  (if (user-exists? user-info db-connection)
    email
    (throw (Exception. "User does not exist!"))))
