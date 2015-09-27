(ns clj-datadog.core-test
  (:require [clojure.test :refer :all]
            [clj-datadog.core :as dd])
  ;; (:use [clj-datadog.core]
  ;;       [clojure.test])
  )

(defmacro should-send-msg
  "Assert that the expected stat is passed to the send-msg method
   the expected number of times."
  [command result]
  `(let [sended# (atom nil)]
     (with-redefs
       [dd/send-msg (fn [value#] (reset! sended# value#))]
       ~command
       (is (= @sended# ~result)))))

(deftest should-send-increment
  (should-send-msg (dd/increment "page.views") "page.views:1|c"))
