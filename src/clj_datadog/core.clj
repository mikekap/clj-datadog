(ns clj-datadog.core
  "A simple StatsD client written in Clojure
   Taken from https://github.com/etsy/statsd/tree/master/examples

   Comments for metrics are taken from official documentation:
   http://docs.datadoghq.com/guides/dogstatsd"
  (:import (java.net InetAddress DatagramPacket DatagramSocket))
  (:gen-class))

(defn format-tags
  "Construcsts statsd-formatted string with tags"
  [tags]
  (if (empty? tags)
    ""
    (let [format-pair (fn [[key value]]
                        (str (name key)
                             (if (some? value) ":")
                             (if (keyword? value) (name value) (str value))))
        tags-list (map format-pair tags)
        reduced-tags (clojure.string/join "," tags-list)]
      (str "|#" reduced-tags))))

(defn- conn
  [conn-spec]
  (let [default {:host "127.0.0.1" :port 8125}]
    (if (seq conn-spec)
      conn-spec
      default)))

;; UDP helper functions

(defn- make-socket
  ([] (DatagramSocket.))
  ([port] (DatagramSocket. port)))

(defn- send-data [send-socket host port data]
  (let [ip-address (InetAddress/getByName host),
        send-packet (DatagramPacket.
                         (.getBytes data)
                         (.length data)
                         ip-address
                         port)]
  (.send send-socket send-packet)))

(defn- send-msg [conn-spec data]
  (let [send-socket (make-socket)
        {:keys [host port]} (conn conn-spec)]
       (send-data send-socket host port data)))

;; StatsD client functions

(defn increment
  "Counters track how many times something happened per second,
   like the number of database requests or page views."
  ([conn-spec metric] (increment conn-spec metric 1 {}))
  ([conn-spec metric arg] (if (map? arg)
                            (increment conn-spec metric 1 arg)
                            (increment conn-spec metric arg {})))
  ([conn-spec metric value tags]
   (send-msg conn-spec (str metric ":" value "|c" (format-tags tags)))))

(comment
  (increment {} "database.query.count")
  (increment {:host "localhost" :port 1234} "page_views.count" 10 {:page "contacts"}))

(defn decrement
  "Shorthand for incrementing by negative values."
  ([conn-spec metric] (decrement conn-spec metric 1 {}))
  ([conn-spec metric arg] (if (map? arg)
                            (decrement conn-spec metric 1 arg)
                            (decrement conn-spec metric arg {})))
  ([conn-spec metric value tags] (increment conn-spec metric (- value) tags)))

(comment
  (decrement {} "comments")
  (decrement {:host "localhost" :port 1234} "users.online" {:group "administrators"}))

(defn gauge
  "Histograms track the statistical distribution of a set of
   values, like the duration of a number of database queries
   or the size of files uploaded by users. Each histogram will
   track the average, the minimum, the maximum, the median,
   the 95th percentile and the count."
  ([conn-spec metric value] (gauge conn-spec metric value {}))
  ([conn-spec metric value tags]
   (send-msg conn-spec (str metric ":" value "|g" (format-tags tags)))))

(comment
  (gauge {} "file.upload.size" (:size file))
  (gauge {:host "localhost" :port 1234} "file.size" (:size file) {:type "upload"}))

(defn timing
  "StatsD only supports histograms for timing, not generic values
   (like the size of uploaded files or the number of rows returned
   from a query). Timers are essentially a special case of histograms,
   so they are treated in the same manner by DogStatsD for
   backwards compatibility.

   You might consider using macro `timed`"
  ([conn-spec metric value] (timing conn-spec metric value {}))
  ([conn-spec metric value tags]
   (send-msg conn-spec (str metric ":" value "|ms" (format-tags tags)))))

(comment
  (timing {} "services.api" elapsed)
  (timing {:host "localhost" :port 1234} "databse.query.time" elapsed {:query "filtered"}))

(defmacro timed
  "Measure time of the execution of the provided body parts,
   and then report measured time.
   Returns value returned by last expression."
  [conn-spec metric tags & body]
  `(let [start# (System/currentTimeMillis)
         result# (do ~@body)
         time# (- (System/currentTimeMillis) start#)]
     (timing ~conn-spec ~metric time# ~tags)
     result#))

(comment
  (timed {} "database.query.time" {:query "find-by-id"}
         (db.find-by-id id)))
