(ns tweets.routes.users-test
  (:require
   [tweets.routes.users :refer :all]
   [clojure.test :refer :all]
   [tweets.db :as db]
   [tweets.fixtures :refer [create-db-context]]))

(use-fixtures :once create-db-context)

(def mock-user
  {:email    "ds2643@columbia.edu"
   :password "potato"})

(deftest simulate-user-authentication
  (testing "No users initially in empty database"
    (is (empty? (get-existing-users db/test-db))))

  (testing "Creating user yields id"
    (let [id (create-user mock-user db/test-db)]
      (is (string? id))))

  (testing "Attempting to recreate user throws an error"
    (is (thrown? Exception (create-user mock-user db/test-db))))

  (testing "Created user exists in users table of db"
    (is (user-exists? mock-user db/test-db)))

  (testing "User never created does not exist"
    (let [fake-user {:email "peter-sagan@bora.com"}]
      (is (not (user-exists? fake-user db/test-db)))))

  (testing "Getting user id yields a valid id"
    (let [user-id (get-user-id mock-user db/test-db)]
      (is (valid-id? user-id db/test-db))))

  (testing "Querying for existing users yields non-empty result"
    (is (seq (get-existing-users db/test-db)))))
