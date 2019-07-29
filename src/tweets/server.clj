(ns tweets.server
  "Implements routing defining server API."
  (:require
   [clojure.string :as str]
   [compojure.core :refer [defroutes POST GET PUT]]
   [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
   [compojure.route :as route]
   [ring.adapter.jetty :as ring]
   [cheshire.core :as json]
   [buddy.auth.backends :as backends]
   [buddy.auth.middleware :refer (wrap-authentication)]
   [buddy.sign.jwt :as jwt]
   [tweets.db :as db]
   [tweets.routes.users :as users]
   [tweets.routes.tweets :as query]))

(def ^:private secret "answer-to-the-great-question")

(def jwt-backend (backends/jws {:secret secret}))

(defn valid-token? [jwt db-connection]
  (when jwt
    (let [id (:id (jwt/unsign jwt secret))]
      (users/valid-id? id db-connection))))

(defroutes routes
  ;; TODO: handle failure error cases
  (POST "/sign-up" req
        (let [user-info (get-in req [:body :user-info])
              id        (users/sign-up user-info db/test-db)
              token     (jwt/sign {:id id} secret)]
          (json/encode {:token token})))

  ;; TODO: handle failure error cases
  (GET "/sign-in" req
        (let [user-info (get-in req [:body :user-info])
              id        (users/sign-in user-info db/test-db)
              token     (jwt/sign {:id id} secret)]
          (json/encode {:token token})))

  (GET "/query-tweets" req
       (let [token (get-in req [:body :token])]
         (if (valid-token? token db/test-db)
           (let [query-params (get-in req [:body :query-params])
                 query-result (query/query-tweets query-params db/test-db)]
             {:body {:query-result query-result}})
           {:body {:msg "Invalid token!"}})))

  (route/not-found "Invalid route"))

(defn run-server
  "Run an instance of the tweets server on the local machine at
  the port indicated in the argument."
  [port]
  (let [config {:port  port :join? false}
        app (-> routes
                (wrap-authentication jwt-backend)
                wrap-json-response
                (wrap-json-body {:keywords? true}))]
    (ring/run-jetty app config)))
