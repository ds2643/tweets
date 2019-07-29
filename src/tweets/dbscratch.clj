;; TODO: probably ready to delete
(ns tweets.dbscratch
  (:require
   [clojure.java.jdbc :as jdbc]))

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/test.db"})

;; TODO: migrate
(defn create-db
  "create db and table"
  []
  (let [command
        (jdbc/create-table-ddl :news
                               [[:timestamp :datetime :default :current_timestamp ]
                                [:url :text]
                                [:title :text]
                                [:body :text]])]
    (try (jdbc/db-do-commands db command)
         (catch Exception e
           (println (.getMessage e))))))

(def testdata
  {:url  "http://example.com",
   :title "SQLite Example",
   :body  "Example using SQLite with Clojure"})

(defn print-result-set
  "prints the result set in tabular form"
  [result-set]
  (doall result-set))

(defn output
  "execute query and return lazy sequence"
  []
  (jdbc/query db ["select * from news"]))

(create-db)

(jdbc/insert! db :news testdata)

(print-result-set (output))
