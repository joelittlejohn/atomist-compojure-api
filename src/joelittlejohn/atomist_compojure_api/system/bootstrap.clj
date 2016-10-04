(ns joelittlejohn.atomist-compojure-api.system.bootstrap
  (:require [com.stuartsierra.component :as component]
            [ring.component.jetty :refer [jetty-server]]
            [clojure.tools.logging :as log])
  (:require [joelittlejohn.atomist-compojure-api.rest.webapp :as webapp])
  (:gen-class))
;
; Component bootstrapping/startup stuff
;

; useful to specify these separately
(def dependencies
  {:rest-api [:config]
   :jetty    {:app :rest-api}})


(defn system-map
  "Create the system map"
  [config]
  (log/info "Creating system map...")
  (component/system-map
    :rest-api (webapp/new-web-app)
    :config config
    :jetty (jetty-server (:jetty config))))

(defn system-using
  "Add dependencies from config"
  [system deps]
  (component/system-using
    system
    deps))

(defn infinite-loop
  "An infinite loop"
  [fn]
  (fn)
  (future (infinite-loop fn))
  nil)



(defn -main
  "Start things up and use main thread to keep things running"
  [& args]
  (component/start
    (system-using
      (system-map {:version "1.0.0"
                   :jetty {:port 8080}}) dependencies))
  (infinite-loop #(do (Thread/sleep 1000))))
