(ns joelittlejohn.atomist-compojure-api.rest.webapp
  (:require [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s])
  (:require [joelittlejohn.atomist-compojure-api.models.status :as m-status]
            [joelittlejohn.atomist-compojure-api.rest.status :as status]))

(declare app-routes)

(defrecord WebApp [config]

  component/Lifecycle

  (start [this]
    (log/info "Starting Web App")
    (assoc this :handler (app-routes this)))

  (stop [this]
    (log/info "Stopping Web App")
    (assoc this :handler nil)))

(s/defn new-web-app :- WebApp
  []
  (map->WebApp {}))

(s/defn app-routes
  [web-app :- WebApp]
  (api
    {:swagger {
               :ui   "/"
               :spec "/swagger.json"
               :data {:info {:title       "Compojure API Sample Service"
                             :version     (-> web-app :config :version)
                             :description "A sample Compojure API based RESTful API"}
                      :tags [{:name        "Status"
                              :description "Status/healthcheck related resources"}]}}}

    (context "/v1" []

      (GET "/status" []
        :tags ["Status"]
        :return m-status/Status
        :summary "A very high level indication of whether or not this service is operation normally"
        (status/get-status (:config web-app))))))
