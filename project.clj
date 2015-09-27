(defproject clj-datadog "0.1.0-SNAPSHOT"
  :description "Clojure client for DataDog service via statsd protocol"
  :url "https://github.com/truckerpathteam/clj-datadog"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [environ "1.0.0"]]

  :main ^:skip-aot clj-datadog.core

  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
