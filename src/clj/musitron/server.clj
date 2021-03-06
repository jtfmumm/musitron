(ns musitron.server
  (:require
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as response])
  (:use [hiccup.core]
           [compojure.core]
           [musitron.core]))

(defn view-layout [& content]
  (html
      [:head
           [:meta {:http-equiv "Content-type"
                        :content "text/html; charset=utf-8"}]
           [:title "Musitron"]]
      [:body content]))

(defn view-content []
  (view-layout
       [:h2 "Musitron"]
       [:p {:id "colorchange"} "You can make this paragraph blue.  Click it!"]
       [:p {:id "clickhere"} "Or get yourself a nice alert by clicking here."]
       [:script {:src "/js/jquery-1.10.2.min.js"}]
       [:script {:src "/js/cljs.js"}]))

(defroutes main-routes
  (GET "/" []
       (view-content))
      (route/resources "/"))

(def app (handler/site main-routes))

;(def -main [& args] ())