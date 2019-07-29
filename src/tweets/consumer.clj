(ns tweets.consumer
  "Implements logic for consuming twitter feed, filtering for hashtags,
  and persistence of tweet data."
  (:require
   [twitter-streaming-client.core :as client]
   [twitter.oauth :as oauth]
   [twitter.api.streaming :as tstreaming]
   [clojure.string :as str]
   [clojure.java.io :as io]
   [clojure.set :as set]
   [clojure.java.jdbc :as jdbc]
   [honeysql.core :as sql]
   [honeysql.helpers :as helpers]
   [tweets.db :as db]))

(defn find-hashtags [text]
  (into #{} (re-seq #"\#\w+" text)))

;; TODO: manage test credentials
;; TODO: clean!
(def my-creds
  (let [app-consumer-key         "IuTTfeKRMK0390EXVTM3uKCEV"
        app-consumer-secret      "WGMqWzkTWAYWtMmdL4g19NNGK5ljXlkC9yDdHBJ8TNCwrEJrPJ"
        user-access-token        "1155215826410180609-JD3z5s46L4W0ZNligesxdKL8nTpYhL"
        user-access-token-secret "ef4annZvIYGYp84nNDgK4JipzjvKp4z2l11OudnwyAUZF"]
    (oauth/make-oauth-creds app-consumer-key  app-consumer-secret
                            user-access-token user-access-token-secret)))

;; TODO: normalize use of hyphen
(def hash-tags #{"tech" "funny" "photography"})

;; TODO: will get duped by something like #technews
(defn make-hashtag-filter [hash-tags]
  (let [ht (into #{} (map (partial str "#") hash-tags))]
    (fn includes-hashtags? [text]
      (boolean (seq (set/intersection ht (find-hashtags text))))))

  #_
  (let [with-sym (mapv (partial str "#") hash-tags)]
    (fn includes-hashtags? [text]
      ;; TODO:  better off with something like...
      ;;       "the overlapping set of hashtags is not empty"
      (boolean (some (partial str/includes? text) with-sym)))))

#_
(let [my-filter (make-hashtag-filter hash-tags)]
  (my-filter "hello #tech"))

(def search-perams
  {:track (str/join "," hash-tags)})

;; TODO: wrap in public api
(defn make-stream []
  (client/create-twitter-stream tstreaming/statuses-filter
                                :oauth-creds my-creds
                                :params search-perams))

#_
(def stream (make-stream))

#_
(client/start-twitter-stream stream)

(defn process-tweets [tweets]
  (for [{:keys [user created_at text]} tweets]
    {:author       (:screen_name user)
     :date-created created_at
     :text         text
     :hash-tags    (find-hashtags text)}))

;; TODO: note ad-hoc filtering solution in issues
(defn get-filtered-tweets [stream]
  (let [includes-hashtags? (make-hashtag-filter hash-tags)
        queue              (client/retrieve-queues stream)
        in-english?        #(= (:lang %) "en")]
    (->> (:tweet queue)
         (filter (comp includes-hashtags? :text))
         (filter in-english?)
         process-tweets)))

#_
(first (get-filtered-tweets stream))


;; TODO: cancel the client : stop collecting tweets
#_
(client/cancel-twitter-stream stream)

#_
(.close debug-file)

;; TODO: add async?
;; TODO: deprecate
#_
(defn consume-tweets []
  (let [stream (make-stream)]
    (do (client/start-twitter-stream stream)
        ;; TODO: routinely poll for changes and update accordingly
        ;; TODO: scaffold with println
        (doseq [n (range 100000)]
          (Thread/sleep 500)
          (let [tweets (get-filtered-tweets stream)]
            (doseq [t tweets]
              (spit (io/file "hello.txt") (str t "\n") :append true)))))))

#_
(def example-tweet
  {:author "Lucas19082", :date-created "Sun Jul 28 04:00:59 +0000 2019", :text "RT @NerolRose: Glow\n\nBy @normyip \n#maleportrait #photography #malephotography #art #sensuality #youth #muscles #asianhunk #AsianEarpers #as…", :hash-tags #{"#youth" "#art" "#photography" "#as" "#AsianEarpers" "#maleportrait" "#sensuality" "#asianhunk" "#muscles" "#malephotography"}})

(defn add-tweet-to-db
  [{:as tweet :keys [author date-created text hash-tags]}
   db-connection]
  (let [format-hashtags (partial str/join ",")
        ;; TODO: make a tweets table
        table           :tweets
        tweet-id        (db/create-random-id table db-connection)
        tweet-data      {:id          tweet-id
                         :hashtags    (format-hashtags hash-tags)
                         :author      author
                         :datecreated date-created
                         :text        text}
        statement       (-> (helpers/insert-into table)
                            (helpers/values [tweet-data])
                            sql/format)]
    (jdbc/execute! db-connection statement)))

#_
(add-tweet-to-db example-tweet db/test-db)

;; TODO: add docstring
;; TODO: integrate upwards
(defn collect-tweets []
  (let [stream (make-stream)]
    (do (client/start-twitter-stream stream)
        (future
          (fn []
            (doseq [n (range)]
              (Thread/sleep 500)
              (doseq [tweet (get-filtered-tweets stream)]
                (add-tweet-to-db tweet db/test-db))))))))

#_
(collect-tweets)

