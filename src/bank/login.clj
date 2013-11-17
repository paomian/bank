(ns bank.login
  (:use [compojure.core]
        [monger.operators]
        [monger.collection       :only [find-one-as-map update]]
        [bank.template           :only [template]]
        [bank.util]
        [hiccup.form])
  (:import [org.jasypt.util.password StrongPasswordEncryptor])
  (:require 
    [ring.util.response           :as response]
    [noir.cookies                 :as cookies]
    [noir.session                 :as session]))
(defn dologin [user pwd]
  (let [result (find-one-as-map "user" {:user user})]
    (if result
      (if (and 
            (.checkPassword (StrongPasswordEncryptor.) pwd  (result :pwd))
            (= (result :alive) 1))
        (do 
          (update "user" {:user user}  {$set {:last-login (java.util.Date.)}})
          (session/put! :user user)
          (log-login user)
          (response/redirect "/"))
        (response/redirect "/err/login-err-page"))
      (response/redirect "/err/login-err-page"))))

(defn dologout []
  (do
    (log-logout (session/get :user))
    (session/clear!)
    (response/redirect "/")))
(defn bye []
  (let [result (find-one-as-map "user" (:user (session/get :user)))]
    (do
      (update "user" {:user (result :user)} {$set {:alive 0}})
      (session/clear!)
      (response/redirect "/"))))
(template login-page [] 
          [:div.container 
           [:form.form-signin {:method "POST" :action "/login"}
            [:table
             [:tr
              [:td (label :user "Username")]
              [:td (text-field :user)]]
             [:tr
              [:td (label :pwd "Password")]
              [:td (password-field  :pwd)]]
             [:tr
              [:td]
              [:td [:button.btn.btn-primary {:type "submit"} "Log In"]]]
             [:tr
              [:td]
              [:td
               [:a {:href "/login/reset"} "密码重置"]]]]]])
#_(template  login-err-page [] 
          [:div.container 
           [:div {:class "alert"} 
            [:strong {} "Warning!"] "用户名或密码错误，请重行登录。"]
           [:form.form-signin {:method "POST" :action "/login"}
            [:table
             [:tr
              [:td (label :user "Username")]
              [:td (text-field :user)]]
             [:tr
              [:td (label :pwd "Password")]
              [:td (password-field  :pwd)]]
             [:tr
              [:td]
              [:td [:button.btn.btn-primary {:type "submit"} "Log In"]]]
             [:tr
              [:td]
              [:td
               [:a {:href "/login/reset"} "密码重置"]]]]]])
(defn calculate [vectorr]
  ((if (and (== (vectorr 0) 1) (== (vectorr 1) 1) (== (vectorr 2) 0))
     1
     0)))
