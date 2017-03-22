(ns truckerpath.clj-datadog.component
  (:require [com.stuartsierra.component :as component]
            [truckerpath.clj-datadog.core :as dd]))

(defrecord Datadog [conn-spec]
  component/Lifecycle
  (start [this]
    (assoc this :conn-spec conn-spec))

  (stop [this]
    (dissoc this :conn-spec)))

(defn datadog
  [conn-spec]
  (->Datadog conn-spec))

(defn increment
  ([datadog metric]
   (dd/increment (:conn-spec datadog) metric))

  ([datadog metric arg]
   (dd/increment (:conn-spec datadog) metric arg))

  ([datadog metric value tags]
   (dd/increment (:conn-spec datadog) metric value tags)))

(defn decrement
  ([datadog metric]
   (dd/decrement (:conn-spec datadog) metric))

  ([datadog metric arg]
   (dd/decrement (:conn-spec datadog) metric arg))

  ([datadog metric value tags]
   (dd/decrement (:conn-spec datadog) metric value tags)))

(defn gauge
  ([datadog metric value]
   (dd/gauge (:conn-spec datadog) metric value))

  ([datadog metric value tags]
   (dd/gauge (:conn-spec datadog) metric value tags)))

(defn timing
  ([datadog metric value]
   (dd/timing (:conn-spec datadog) metric value))

  ([datadog metric value tags]
   (dd/timing (:conn-spec datadog) metric value tags)))

(defmacro timed
  [datadog metric tags & body]
  `(let [start# (System/currentTimeMillis)
         result# (do ~@body)
         time# (format "%f" (/ (- (System/nanoTime) start#) 1000000.0))]
     (timing ~datadog ~metric time# ~tags)
     result#))
