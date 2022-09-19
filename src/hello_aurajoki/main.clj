(ns hello-aurajoki.main
  (:gen-class)
  (:require [aleph.http :as http]
            [reitit.ring :as ring]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [muuntaja.core :as m]
            [hello-aurajoki.forecast :as forecast])
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


(defn- forecast-handler [{:keys [path-params]}]
  (if-let [location (forecast/get-location (:city path-params))]
    {:status 200
     :body (forecast/average-temperature-forecast location)}
    {:status 404
     :body {:error (str (:city path-params) " not found!")}}))

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
