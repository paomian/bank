(ns bank.user
  (:use [monger.collection           :only [find-one-as-map update]]
        [bank.template               :only [template]]
        [hiccup.form]
        [bank.util])
  (:require
    [ring.util.response              :as response]
    [noir.session                    :as session]))
(template profile [result]
          [:div {:class "container-fluid"} 
           [:div {:class "row-fluid"}
            [:div {:class "span12"}
             [:h3 {:class "text-center"} (str  (result :user) "欢迎来到银行管理系统")]
             [:p {:class "text-success text-center"}
              [:strong  "这里将为你提供基本的理财服务。"]]
             [:table {:class "table table-hover table-condensed table-bordered"}
              [:thead 
               [:tr 
                [:th  "用户名"]
                [:th  "账户金额"]
                [:th  "上次登录"]]]
              [:tbody 
               [:tr 
                (for [x
                      [(result :user)
                       (result :value)
                       (result :last-login)]]
                  [:th (str x)])]]]
             [:form {:class "form-inline" :method "POST" :action "/in"}
              (text-field {:class "span6", :type "text", :placeholder "请输入存入金额(必须为数字)"} :invalue)
              [:button {:type "submit", :class "btn"} "存入"]]
             [:form {:class "form-inline" :method "POST" :action "/out"}
              (text-field {:class "span6", :type "text", :placeholder "请输入取出金额(必须为数字)"} :outvalue)
              [:button {:type "submit", :class "btn"} "取出"]]
             [:form {:class "form-inline" :method "POST" :action "/transger"}
              (text-field {:class "span3", :type "text", :placeholder "请输入转账金额(必须为数字)"} :value)
              (text-field {:class "span3", :type "text", :placeholder "请输入转入用户(账户用户名)"} :touser)
              [:button {:type "submit", :class "btn"} "转账"]]
             [:div {:class "row-fluid"}
              [:div {:class "span5"}
               [:button {:class "btn btn-block btn-primary btn-large", :type "button"} "日志"]]
              [:div {:class "span2"}]
              [:div {:class "span5"}
               [:button {:class "btn btn-large btn-block btn-primary", :type "button"} "销户"]]]]]])
(defn show-profile []
  (is-user
    (let [result (getuser (session/get :user))]
      (profile result))))
(defn in [value]
  (is-user 
    (let [result (getuser (session/get :user))
          number (valid-num value result)]
      (if number
        (do
          (db-in number)
          (log-in (session/get :user) number)))
      (response/redirect "/in-err"))))
(defn out [value]
  (is-user
    (let [result (getuser (session/get :user))
          number (valid-num value result)]
      (if number
        (do
          (db-out number)
          (log-out (session/get :user) number))
        (response/redirect "/out-err")))))
(defn transger [touser value]
  (is-user
    (let [result (getuser (session/get :user))
          number (valid-num value result)]
      (if number
        (do
          (db-transfer touser number)
          (log-transfer (session/get :user) touser number))
        (response/redirect "/transger-err")))))
(defn show-log []
  (is-user
    ()))
