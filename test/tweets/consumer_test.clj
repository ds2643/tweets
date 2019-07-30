(ns tweets.consumer-test
  (:require
   [tweets.consumer :refer :all]
   [clojure.test :refer :all]
   [tweets.db :as db]
   [tweets.fixtures :refer [create-db-context]]))

(use-fixtures :once create-db-context)

(def test-hashtags #{"biking" "tour-de-france"})

(deftest utility-functions
  (testing "hashtag filtering"
    (let [includes-test-hashtags?
          (make-hashtag-filter test-hashtags)]
      (is (includes-test-hashtags? "hello #tech #biking")))))
