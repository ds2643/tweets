(defproject tweets "0.1.0-SNAPSHOT"
  :description "REST service allowing persistence and querying of tweets."
  :dependencies
  [[cheshire "5.8.1"]
   [clj-http "3.9.1"]
   [compojure "1.6.1"]
   [honeysql "0.9.4"]
   [metosin/compojure-api "2.0.0-alpha29"]
   [org.clojure/clojure "1.8.0"]
   [org.clojure/java.jdbc "0.7.9"]
   [org.xerial/sqlite-jdbc "3.28.0"]
   [ragtime "0.8.0"]
   [re-rand "0.1.0"]
   [ring/ring-jetty-adapter "1.4.0"]
   [ring/ring-json "0.4.0"]

   ;; TODO: choose one of two
   [twitter-api "1.8.0"]
   ;; TODO: vet through testing
   [twitter-streaming-client "0.3.3"]
   [twttr "3.2.2"]]

  :aliases
  {"migrate"        ["run" "-m" "tweets.migration/migrate"]
   "rollback"       ["run" "-m" "tweets.migration/rollback"]}

  :main tweets.main
  :profiles
  {:dev
   {:plugins
    [[jonase/eastwood "0.3.5"]]}})
