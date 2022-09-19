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
     :headers {"Content-Type" "text/plain; charset=utf-8"}
     :body (str "Hello Aurajoki Overflow"
                (when friend
                  (str " and " friend))
                "!\n\n"
                (OffsetDateTime/now))}))

; https://open-meteo.com/en/docs
(def forecast-url-prefix
  "https://api.open-meteo.com/v1/forecast?hourly=temperature_2m&")
; https://open-meteo.com/en/docs/geocoding-api
(def geocoding-url-prefix
  "https://geocoding-api.open-meteo.com/v1/search?name=")

(defn- api-get [url]
  (-> @(http/get url)
    :body
    bs/to-string
    (j/read-value j/keyword-keys-object-mapper)))

(defn- get-location [city-name]
  (-> (str geocoding-url-prefix city-name)
      api-get
      :results
      first))

(defn- average-temperature-forecast [{:keys [latitude longitude name
                                             country_code elevation]}]
  (let [forecast-url (str forecast-url-prefix
                          "latitude=" latitude
                          "&longitude=" longitude)
        response (api-get forecast-url)
        temperatures (-> response
                         :hourly
                         :temperature_2m)
        times (-> response
                  :hourly
                  :time
                  sort)]
    {:average-temperature-forecast (/ (reduce + temperatures) (count temperatures))
     :time-start (first times)
     :time-end (last times)
     :city name
     :elevation elevation
     :country country_code}))

(defn- forecast-handler [{:keys [path-params]}]
  (let [location (get-location (:city path-params))
        forecast (average-temperature-forecast location)]
    {:status 200
     :body forecast}))

;; Routes and middleware

(def routes
  [["/" {:get {:handler home-handler}}]
   ["/api/forecast/:city" {:get {:handler forecast-handler}}]])

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
