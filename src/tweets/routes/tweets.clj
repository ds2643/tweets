(ns tweets.routes.tweets
  "Contains logic supporting `query-tweets` route."
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.string :as str]
   [honeysql.core :as sql]))

(defn includes-hashtag? [hashtag tweet]
  (let [hashtag-set (into #{} (str/split (:hashtags tweet) #"\,"))]
    (contains? hashtag-set hashtag)))

;; NOTE: Rather than filtering in the query by compiling a `where`
;;       clause, _all_ the contents of the tweet table are loaded
;;       into memory, then filtered in Clojure run-time.
;;
;;       While such a choice might help ergonomics from the perspective
;;       of the application maintainer (avoid writing transformation
;;       layer into sql `where` clauses), this behavior might be fairly
;;       spatial inefficient for large tweet tables (e.g., when `consumer`
;;       has been running for a while).
;;
;;       Alternative: filter in sql query.
(defn get-all-tweet-content [db-connection]
  (let [query (sql/format {:select [:author :hashtags :text :datecreated]
                           :from   [:tweets]})]
    (into [] (jdbc/query db-connection query))))

(defn query-tweets
  "Given a set of query-parameters representing search criteria,
  filters the content of the `test-db` `tweets` table for matches.

  Please note that omitting `query-parameters` yields the entire
  `tweets` table.

  Please also note that:
  1. Querying is done over single entities (e.g., single hashtag)
  rather than collections.
  2. Filtering isn't implemented for timestamps or text. "
  [{:keys [hashtag author] :as query-params}
   db-connection]
  (cond->> (get-all-tweet-content db-connection)
    hashtag (filter (partial includes-hashtag? hashtag))
    author  (filter #(= (:author %) author))))
