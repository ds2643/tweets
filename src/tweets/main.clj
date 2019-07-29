(ns tweets.main
  "Entry-point for running tweets server"
  (:require
   [tweets.server :refer [run-server]]))

;; TODO: add logic to start consumer
(defn -main [& args]
  (let [server-port
        ;; TODO: note default in documentation
        (if-let [env-port (System/getenv "API_PORT")]
          (Integer/parseInt env-port)
          3666)]
    (println (format "Starting server on %s" server-port))
    (run-server server-port)))
