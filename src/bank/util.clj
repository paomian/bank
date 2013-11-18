(ns bank.util
  (:use
    [hiccup core page]
    [monger.operators]
    [monger.core       :only [connect-via-uri!]]
    [monger.collection :only [insert find-one-as-map update find-maps]]
    [bank.template     :only [template]]
    [noir.validation   :only [valid-number?]])
  (:require
    [ring.util.response           :as response]
    [noir.session                 :as session]
    [monger.query                 :as mongo]))
(defn getuser [user] (find-one-as-map "user" {:user user}))
(defn getuserlog [user] (mongo/with-collection "userlog" 
                                         (mongo/find {:user user})
                                         (mongo/sort {:time -1})))
(defn act [] (java.util.Date.))
(defn log
  ([user action] (insert "userlog" {:time (act) :action action :user user}))
  ([user action expr] (insert "userlog" (merge {:time (act) :action action :user user} expr))))
;;三种账户操作
(defn db-in [value]
  (let [user (session/get :user)]
    (update "user" {:user user} {$inc {:value value}})))
(defn db-out [value]
  (let [user (session/get :user)]
    (update "user" {:user user} {$inc {:value (- value)}})))
(defn db-transfer [touser value]
  (let [user (session/get :user)]
    (update "user" {:user user} {$inc {:value (- value)}})
    (update "user" {:user touser} {$inc {:value value}})))
;;验证数字正确性
(defn valid-num [number result]
  (if (valid-number? number)
    (if (<= (Long/valueOf number) (:value result))
      (Long/valueOf number)
      false)
    false))
;;登录日志
(defn log-login [user]
  (log user "login" ))
;;转账日志
(defn log-transfer [usera userb value]
  (log usera "transferout" {:value value})
  (log userb "transferin" {:value value}))
;;存钱日志
(defn log-in [user value]
  (log user "input" {:value value}))
;;取钱日志
(defn log-out [user value]
  (log user "output" {:value value}))
;;销户日志
(defn log-bye [user]
  (log user "bye"))
;;注销日志
(defn log-logout [user]
  (log user "logout"))
;;准备数据库
(defn prepare-mongo []
  (do
    (connect-via-uri! "mongodb://bank:bank2013@127.0.0.1:27017/bank")))
#_(defmacro is-auth? [& code]
  `(let [(gensym user) user]
     (if (user)
       (do ~@code)
       (response/redirect "/"))))
(defmacro is-admin [& code]
  `(if (= (session/get :admin) "admin")
     (do ~@code)
     (response/redirect "/")))
(defmacro is-user [& code]
  `(if (session/get :user)
     (do ~@code)
     (response/redirect "/")))
(template index []
          [:div.container-narrow
           [:hr]
           [:div.jumbotron
            [:h2 "欢迎来到银行管理!"]
            [:br]
            [:br]
            [:br]
            [:br]
            [:br]
            (let [user (session/get :user)]
              (if user
                [:a.btn.btn-large.btn-danger {:href (str "/profile" )} "个人空间"]
                [:p
                  [:a#login.btn.btn-large.btn-info {:href "/login"} "登陆"]
                 "&nbsp;&nbsp;&nbsp;&nbsp;"
                  [:a.btn.btn-large.btn-inverse {:href "/reg"} "注册"]]))]])
