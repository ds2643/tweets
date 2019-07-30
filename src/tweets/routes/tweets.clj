(ns tweets.routes.tweets
  "Contains logic supporting `query-tweets` route."
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.string :as str]
   [honeysql.core :as sql]
   ;; TODO: dev dependency
   [tweets.db :as db]))

(defn includes-hashtag? [hashtag tweet]
  (let [hashtag-set (into #{} (str/split (:hashtags tweet) #"\,"))]
    (contains? hashtag-set hashtag)))

;; TODO: note in issues the inefficiency of doing this in memory
(defn get-all-tweet-content [db-connection]
  (let [query (sql/format {:select [:author :hashtags :text :datecreated]
                           :from   [:tweets]})]
    (into [] (jdbc/query db-connection query))))

;; TODO: note nothing is done with timestamp/text
;; TODO: write doc-string
(defn query-tweets
  [{:keys [hashtag author] :as query-params}
   db-connection]
  (cond->> (get-all-tweet-content db/test-db)
    hashtag (filter (partial includes-hashtag? hashtag))
    author  (filter #(= (:author %) author))))

#_
(query-tweets {:author "zhiraamani"} db/test-db)

#_
(query-tweets {:hashtag "#photography"} db/test-db)
