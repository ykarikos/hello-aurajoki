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
(def forecast-url
  "https://api.open-meteo.com/v1/forecast?latitude=60.45148&longitude=22.26869&hourly=temperature_2m")

(defn- average-temperature-forecast []
  (let [response (-> @(http/get forecast-url)
                     :body
                     bs/to-string
                     (j/read-value j/keyword-keys-object-mapper))
        temperatures (-> response
                         :hourly
                         :temperature_2m)]
    (/ (reduce + temperatures) (count temperatures))))

(defn- forecast-handler [_]
  {:status 200
   :body {:average-temperature-forecast (average-temperature-forecast)}})

;; Routes and middleware

(def routes
  [["/" {:get {:handler home-handler}}]
   ["/api/forecast" {:get {:handler forecast-handler}}]])

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
