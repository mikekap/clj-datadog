(ns truckerpath.clj-datadog.core-test
  (:require [expectations :refer [expect]]
            [truckerpath.clj-datadog.core :as dd]
            [truckerpath.clj-datadog.component :as dd-comp]))

(defmacro last-sent-msg
  "Returns last data that was sent to `dd/send-msg`
   during execution of the body"
  [& body]
  `(let [sended# (atom nil)]
     (with-redefs
       [dd/send-msg (fn [_# value#] (reset! sended# value#))]
       ~@body
       @sended#)))

;; dd/format-tags
(expect "|#site:main"
        (dd/format-tags {:site :main}))
(expect "|#page:contacts"
        (dd/format-tags {:page "contacts"}))
(expect "|#status:200"
        (dd/format-tags {:status 200}))
(expect "|#error"
        (dd/format-tags {:error nil}))
(expect "|#site:main,page:contacts,status:200"
        (dd/format-tags {:site :main, :page "contacts", "status" 200}))

;; dd/increment
(expect "page.views:1|c"
        (last-sent-msg (dd/increment {} "page.views")))
(expect "page.views:7|c"
        (last-sent-msg (dd/increment {} "page.views" 7)))
(expect "page.views:1|c|#page:contacts"
        (last-sent-msg (dd/increment {} "page.views" {:page "contacts"})))
(expect "page.views:8|c|#page:contacts"
        (last-sent-msg (dd/increment {} "page.views" 8 {:page "contacts"})))

;; dd/decrement
(expect "users.online:-1|c"
        (last-sent-msg (dd/decrement {} "users.online")))
(expect "users.online:-3|c"
        (last-sent-msg (dd/decrement {} "users.online" 3)))
(expect "users.online:-1|c|#group:admins"
        (last-sent-msg (dd/decrement {} "users.online" {:group "admins"})))
(expect "users.online:-5|c|#group:admins"
        (last-sent-msg (dd/decrement {} "users.online" 5 {:group "admins"})))

;; dd/gauge
(expect "total.posts:526|g"
        (last-sent-msg (dd/gauge {} "total.posts" 526)))
(expect "total.posts:526|g|#site:main"
        (last-sent-msg (dd/gauge {} "total.posts" 526 {:site "main"})))

;; dd/timing
(expect "db.query.time:576|ms"
        (last-sent-msg (dd/timing {} "db.query.time" 576)))
(expect "db.query.time:843|ms|#query:find-by-id"
        (last-sent-msg (dd/timing {} "db.query.time" 843 {:query "find-by-id"})))

;; We tested that `dd/timing` sends data correctly
;; So we know it works in next test, so we wont chack value
(expect #"something.done:\d*\|ms\|#tag:present"
        (last-sent-msg (dd/timed {} "something.done" {:tag "present"}
                                 (do (str "Whatever passed -"
                                          "is passed")))))
(expect #"something.done:\d*\|ms"
        (last-sent-msg (dd/timed {} "something.done" {}
                                 (do (str "Whatever passed -"
                                          "is passed")))))
