(defproject bank "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.6"]
                 [lib-noir "0.7.5"]
                 [hiccup "1.0.4"]
                 [hickory "0.5.1"]
                 [org.jasypt/jasypt "1.7"]
                 [ring/ring-core "1.2.1"]
                 ;;[clj-time "0.6.0"]
                 [com.novemberain/monger "1.5.0"]]
  :plugins [[lein-ring "0.8.8"]]
  :ring {:handler bank.handler/app
         :init bank.util/prepare-mongo}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
