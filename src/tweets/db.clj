(ns tweets.db
  (:require
   [clojure.java.jdbc :as jdbc]
   [re-rand :refer [re-rand]]))

;; TODO: migrate from dbscratch

(def test-db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/test.db"})

(defn create-random-id [table db-connection]
  {:post [(string? %)]}
  (let [existing-ids
        (->> [(format "select id from %s" (name table))]
             (jdbc/query test-db) (map :id) set)
        find-unique-id
        (partial some #(when-not (contains? existing-ids %) %))
        generate-id #(re-rand #"\w{5}\-\w{5}-\w{5}")]
    (find-unique-id (repeatedly generate-id))))
