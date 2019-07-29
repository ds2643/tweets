(ns tweets.migration
  "Management of schema migrations and rollbacks via ragtime."
  (:require
   [ragtime.jdbc :as rconf]
   [ragtime.repl :as rr]
   [tweets.db :as db]))

(defn create-config [db-connection]
  {:datastore  (rconf/sql-database db-connection)
   :migrations (rconf/load-resources "migrations")})

;; NOTE: hard-coding the identity of the tables represents
;;       a quick monkey-patch for getting rollbacks more
;;       tightly automated.
;;
;;       The rollback mechanism exposed by ragtime requires
;;       sequential manual runs: Only one table is rolled
;;       back at a time. A more robust mechanism for finding
;;       the number of partial rollbacks to perform should
;;       be explored.
(def tables #{:users :tweets})

(defn migrate []
  (-> db/test-db create-config rr/migrate))

(defn rollback []
  (doseq [table tables]
    (-> db/test-db create-config rr/rollback)))
