(ns hello-aurajoki.main
  (:gen-class)
  (:require [aleph.http :as http]
            [reitit.ring :as ring]
            [clj-commons.byte-streams :as bs]
            [jsonista.core :as j]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [muuntaja.core :as m])
  (:import [java.time OffsetDateTime]))

;; Handlers

(defn home-handler [{:keys [params] :as request}]
  (def r request)
  (let [friend (get params "friend")]
    {:status 200
     :body (str "Hello world"
                (when friend
                  (str " and " friend))
                "!\n"
                (OffsetDateTime/now))}))

(def weather-url "https://api.open-meteo.com/v1/forecast?hourly=temperature_2m&")
(def geocoding-url "https://geocoding-api.open-meteo.com/v1/search?name=")

(defn api-get [url]
  (-> @(http/get url)
      :body
      bs/to-string
      (j/read-value j/keyword-keys-object-mapper)))

(defn get-location [city]
  (-> (api-get (str geocoding-url city))
      :results
      first))

(defn get-avg-temperature [{:keys [:latitude :longitude]}]
  (let [response (api-get (str weather-url
                            "latitude=" latitude
                            "&longitude=" longitude))
        times (some-> response
                  :hourly
                  :time)
        temperatures (some-> response
                         :hourly
                         :temperature_2m)]
    {:avg-temperature (/ (reduce + temperatures) (count temperatures))
     :start-date (first times)
     :end-date (last times)}))

(defn temperature-handler [{:keys [path-params]}]
  {:status 200
   :body {:temperature (get-avg-temperature (get-location (:city path-params)))}})

;; Routes and middleware

(def routes
  [["/" {:get {:handler home-handler}}]
   ["/api/average-temperature/:city" {:get {:handler temperature-handler}}]])

(def ring-opts
  {:data
   {:muuntaja m/instance
    :middleware [parameters/parameters-middleware
                 muuntaja/format-middleware]}})

(def app
  (ring/ring-handler
   (ring/router routes ring-opts)))

;; Web server

(defonce server (atom nil))

(def port
  (-> (System/getenv "PORT")
      (or "3000")
      (Integer/parseInt)))

(defn start-server []
  (reset! server (http/start-server #'app {:port port})))

(defn stop-server []
  (when @server
    (.close ^java.io.Closeable @server)))

(defn restart-server []
  (stop-server)
  (start-server))

;; Application entrypoint

(defn -main [& args]
  (println (format "Starting webserver on port: %s." port))
  (start-server))
