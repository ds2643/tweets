(ns tweets.server
  "Implements routing defining server API."
  (:require
   [compojure.core :refer [defroutes POST GET PUT]]
   [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
   [compojure.route :as route]
   [ring.adapter.jetty :as ring]
   [clojure.string :as str]
   [tweets.db :as db]
   [tweets.routes.users :as users]
   [tweets.routes.tweets :as query]))

;; TODO: add jwt middleware
(defroutes routes
  (POST "/sign-up" req
        (let [user-info (get-in req [:body :user-info])]
          (users/sign-up user-info db/test-db)))

  ;; TODO: get?
  (POST "/sign-in" req
        (let [user-info (get-in req [:body :user-info])]
          (users/sign-in user-info db/test-db)))

  (GET "/query-tweets" req
       (let [query-params (get-in req [:body :query-params])
             query-result (query/query-tweets query-params db/test-db)]
         {:body {:query-result query-result}}))

  (route/not-found "Invalid route"))

(defn run-server
  "Run an instance of the tweets server on the local machine at
  the port indicated in the argument."
  [port]
  (let [config {:port  port :join? false}
        app (-> routes
                (wrap-json-body {:keywords? true})
                wrap-json-response)]
    (ring/run-jetty app config)))
