# clj-datadog

`clj-datadog` is a client for [DataDog](https://www.datadoghq.com) service
for the [Clojure](http://clojure.org) programming language.

[![Circle CI](https://circleci.com/gh/truckerpathteam/clj-datadog.svg?style=shield)](https://circleci.com/gh/truckerpathteam/clj-datadog)

[![Clojars Project](http://clojars.org/clj-datadog/latest-version.svg)](https://clojars.org/clj-datadog)

## Installation

Include the following dependency in your project.clj file:

    :dependencies [[clj-datadog "1.0.0"]]


## Example Usage

To start tracking events and sending them to DataDog, you firstly have
to register account and install
[DataGog agent](http://docs.datadoghq.com/guides/basic_agent_usage/)
that will start StatsD server on the machine you are running your
application.

Then import datadog in your namespace:

    (require '[clj-datadog.core :as dd])

Following datadog metric methods are available:

### Counters

You can use iether amount or DataDog tags or both.
Decrements are completely symmentrical to increments but
with negative values.


    (dd/increment "page.views")
    (dd/increment "page.views" 10)
    (dd/increment "error.count" {:page "products"})
    (dd/increment "active.connections" 3 {:service "db"})

    (dd/decrement "users.online")
    (dd/decrement "users.online" {:group "admins"})

### Gauges

Gauges require value to be specified, but tags can be omitted

    (dd/gauge "total.posts" 526)
    (dd/gauge "total.posts" 526 {:site "main"})

### Timers

You can report time directly or using a macro that
will do reporting as a side-effect.

In second case tags are required, even if empty.

    (dd/timing "db.query.time" 843 {:query "find-by-id"})
    (dd/timed "external.service.call" {:service service}
              (http/get remote-uri {:socket-timeout timeout}))


## Options

By default this library sends all data to `127.0.0.1:8125`, but
this behaviour can be changed by providing environment variables,
like so:

    DATADOG_HOST=8.8.8.8 \
    DATADOG_PORT=2390 \
    java -jar app.jar

or via Java system properties:

    java -jar app.jar \
    -Ddatadog.host=8.8.8.8 \
    -Ddatadog.port=2390

Parameters handling is done via [environ](https://github.com/weavejester/environ) library

## License

Copyright © 2015 TruckerPath

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
