(ns tweets.fixtures
  (:require
   [tweets.migration :as m]
   [tweets.server :as s]
   [tweets.db :as db])
  (:import
   (java.net ServerSocket)))

(defn create-db-context [f]
  (m/rollback)
  (m/migrate)
  (f)
  (m/rollback)
  (m/migrate))

(defn- find-free-local-port []
  (let [socket (ServerSocket. 0)]
    (let [port (.getLocalPort socket)]
      (.close socket)
      port)))

(def ^:dynamic *local-port* nil)

(defn create-server-context [f]
  (binding [*local-port* (find-free-local-port)]
    (let [test-server (s/run-server *local-port*)]
      (f)
      (.stop test-server))))
