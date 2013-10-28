(ns musitron.core
  (:require [overtone.core :as ot]))

(ot/boot-external-server)

(definst newinst [] (saw 220))



