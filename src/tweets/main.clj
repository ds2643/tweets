(ns tweets.main
  "Entry-point for running tweets server"
  (:require
   [tweets.server :refer [run-server]]
   [tweets.consumer :refer [collect-tweets]]
   [tweets.db :as db])
  (:gen-class))

(defn -main [& args]
  (collect-tweets db/test-db)
  (let [server-port
        (if-let [env-port (System/getenv "API_PORT")]
          (Integer/parseInt env-port)
          3666)]
    (println (format "Starting server on %s" server-port))
    (run-server server-port)))
