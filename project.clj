(defproject hello-aurajoki "0.0.1-SNAPSHOT"
  :description "Simple webapp"
  :url ""
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [metosin/reitit "0.5.18"]
                 [ring "1.9.6"]
                 [ring/ring-defaults "0.3.3"]
                 [aleph "0.5.0"]]
  :main ^:skip-aot hello-aurajoki.main
  :profiles {:uberjar {:aot :all
                       :uberjar-name "hello-aurajoki.jar"}})
