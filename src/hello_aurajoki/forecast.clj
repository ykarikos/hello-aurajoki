(ns hello-aurajoki.forecast
  (:require [aleph.http :as http]
            [clj-commons.byte-streams :as bs]
            [jsonista.core :as j]))

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

(defn get-location [city-name]
  (-> (str geocoding-url-prefix city-name)
      api-get
      :results
      first))

(defn average-temperature-forecast [{:keys [latitude longitude name
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
