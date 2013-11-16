(ns bank.register
  (:import [org.jasypt.util.password StrongPasswordEncryptor]
           [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern])
  (:require
    [noir.session            :as session]
    [ring.util.response      :as response])
  (:use
    [noir.validation]
    [hiccup core page]
    [hiccup.form]
    [monger.core             :only [connect connect! set-db! get-db]]
    [monger.collection       :only [insert insert-batch]]
    [bank.template           :only [template]]))
(defn doregister
  [user pwd r-pwd email]
  (if (and (= pwd r-pwd)) 
    (do 
      (future 
        (insert "user" {:_id (ObjectId.) :user user :nickname user :pwd (.encryptPassword (StrongPasswordEncryptor.) pwd) :email email :register-time (java.util.Date.) :value 0}))
      (response/redirect "/"))))
(template register []
          [:div.container 
           [:form.form-signin {:method "POST" :action "/reg"}
            [:table
             [:tr
              [:td (label :user "Username")]
              [:td (text-field :user)]]
             [:tr
              [:td (label :pwd "Password")]
              [:td (password-field  :pwd)]]
             [:tr
              [:td (label :r-pwd "Repeat Password")]
              [:td (password-field :r-pwd)]]
             [:tr
              [:td (label :email "Email")]
              [:td (text-field :email)]]
             [:tr
              [:td]
              [:td [:button.btn.btn-primary {:type "submit"} "Register"]]]]]])
