(ns bank.handler
  (:use compojure.core
        [bank.login            :only [dologin login-page dologout bye]]
        [bank.register         :only [register doregister]]
        [bank.user]
        [bank.util             :only [index]])
  (:require [compojure.handler :as handler]
            [compojure.route   :as route]
            [noir.session      :as session]))

(defroutes app-routes
  (GET "/" [] (index))
  (GET "/login" [] (login-page))
  (POST "/login" [user pwd] (dologin user pwd))
  (GET "/reg" [] (register))
  (POST "/reg" [user pwd r-pwd email] (doregister user pwd r-pwd email))
  (GET "/logout" [] (dologout))
  ;;(GET "/transger" [] (transger-page))
  (POST "/transger" [touser value] (transger touser value))
  ;;(GET "/in" [] (in-page))
  (POST "/in" [invalue] (in invalue))
  ;;(GET "/out" [] (out-page))
  (POST "/out" [outvalue] (out outvalue))
  ;;(GET "/:user" [user] (info-user))
  (GET "/profile" [] (show-profile))
  (GET "/log" [] (show-log))
  (GET "/bye" [] (bye))
  #_(context "/err" []
           (GET "/login-err" [] (login-err-page))
           (GET "/in-err" [] (in-err-page))
           (GET "/out-err" [] (out-err-page))
           (GET "/transger-err" [] )
           )
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (->
    (handler/site app-routes)
    (session/wrap-noir-flash)
    (session/wrap-noir-session)))
