(defproject musitron "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
  				       [overtone "0.8.1" :exclusions [org.clojure/clojure]]
                 [org.clojure/clojurescript "0.0-1978"]
                 [compojure "1.1.5"]
                 [jayq "2.4.0"]
                 [hiccup "1.0.4"]
                 [org.clojure/math.numeric-tower "0.0.2"]]
  :source-paths ["src/clj"]
  :plugins [[lein-cljsbuild "0.3.4"]
                 [lein-ring "0.8.7"]]
:cljsbuild
{:builds
 [{:source-paths ["src/cljs"],
   :id "main",
   :compiler
   {:pretty-print true,
    :output-to "resources/public/js/cljs.js",
    :optimizations :simple}}]}
:main musitron.server
:ring {:handler musitron.server/app})

