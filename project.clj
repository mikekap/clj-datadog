(defproject clj-datadog "2.0.0"
  :description "Clojure client for DataDog service via statsd protocol"
  :url "https://github.com/truckerpathteam/clj-datadog"

  :dependencies [[org.clojure/clojure "1.7.0"]]

  :main ^:skip-aot clj-datadog.core

  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[expectations "2.0.9"]]}}

  :plugins [[lein-expectations "0.0.8"]]

  ;; Artifact deployment info
  :scm {:name "git"
        :url "https://github.com/truckerpathteam/clj-datadog"}

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :deploy-repositories [["clojars" {:creds :gpg}]]

  :pom-addition [:developers [:developer
                              [:name "Anton Chebotaev"]
                              [:url "http://chebotaev.me"]
                              [:email "anton.chebotaev@gmail.com"]
                              [:timezone "+3"]]])
