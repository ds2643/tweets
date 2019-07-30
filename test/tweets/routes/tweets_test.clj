(ns tweets.routes.tweets-test
  (:require
   [tweets.routes.tweets :refer :all]
   [clojure.test :refer :all]
   [tweets.db :as db]
   [tweets.consumer :as consumer]
   [tweets.fixtures :refer [create-db-context]]))

(use-fixtures :once create-db-context)

(def test-tweet
  {:author       "Lucas19082",
   :date-created "Sun Jul 28 04:00:59 +0000 2019",
   :text         "I enjoy #photography",
   :hashtags     #{"#photography"}})

(deftest hashtag-predicate
  (let [awesome-tweet {:hashtags "#potato"}]
    (is (includes-hashtag? "#potato" awesome-tweet))))

(deftest simulate-querying-of-tweets
  (consumer/add-tweet-to-db test-tweet db/test-db)

  (testing "Searching by for users tweets yields mock data"
    (let [res (query-tweets {:author "Lucas19082"} db/test-db)]
      (is (seq? res))))

  (testing "Searching by for users tweets yields mock data"
    (let [res (query-tweets {:hashtags "#photography"} db/test-db)]
      (is (seq res))))

  (testing "Querying without search parameters yields result"
    (let [res (query-tweets {} db/test-db)]
      (is (seq res)))))
