(ns bank.user
  (:use [monger.collection           :only [find-one-as-map update]]
        [bank.template               :only [template]]
        [hiccup.form]
        [bank.util])
  (:require
    [ring.util.response              :as response]
    [noir.session                    :as session]))
(template profile [result]
          [:div.container 
           [:div {:class "row-fluid"}
            [:div {:class "span12"}
             [:h3 {:class "text-center"} (str  (result :user) "欢迎来到银行管理系统")]
             [:p {:class "text-success text-center"}
              [:strong  "这里将为你提供基本的理财服务。"]]
             (session/flash-get :error)
             (session/flash-get :success)
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
              [:span "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"]
              (text-field {:class "span3", :type "text", :placeholder "请输入转入用户(账户用户名)"} :touser)
              [:button {:type "submit", :class "btn"} "转账"]]
             [:div {:class "row-fluid"}
              [:div {:class "span5"}
               [:a {:class "btn btn-block btn-primary btn-large", :type "button" :href "/log"} "日志"]]
              [:div {:class "span2"}]
              [:div {:class "span5"}
               [:a {:class "btn btn-large btn-block btn-primary", :type "button"} "销户"]]]]]])
(template log-page [result]
          [:div.container
          [:table {:class "table table-hover table-condensed table-bordered"}
           [:thead 
            [:tr 
             [:th  "用户名"]
             [:th  "操作"]
             [:th  "操作时间"]
             [:th  "金额"]]]
           [:tbody 
            (for [one result]
              [:tr 
               (for [x [(one :user)
                        (one :time)
                        (cond 
                          (= (one :action) "input") "存入"
                          (= (one :action) "output") "存出"
                          (= (one :action) "login") "登录"
                          (= (one :action) "logout") "登出"
                          (= (one :action) "transferin") "转入"
                          (= (one :action) "transferout") "转出")
                        (one :value)]]
                 [:th (str x)])])]]])
(defn show-log []
  (is-user
    (let [result (getuserlog (session/get :user))]
      (log-page result))))
(defn err-box [msg] 
  [:div {:class "alert alert-error"}
   [:button {:type "button", :class "close", :data-dismiss "alert"} "&times;"] msg ])
(defn suc-box [msg] 
  [:div {:class "alert alert-success"}
   [:button {:type "button", :class "close", :data-dismiss "alert"} "&times;"] msg ])
(defn flash-err [msg url]
  (do
     (session/flash-put! :error (err-box msg))
     (response/redirect url)))
(defn flash-suc [msg url]
  (do
     (session/flash-put! :success (suc-box msg))
     (response/redirect url)))
(defn show-profile []
  (is-user
    (let [result (getuser (session/get :user))]
      (profile result))))
(defn in [invalue]
  (is-user 
    (let [result (getuser (session/get :user))
          number (valid-num invalue {:value 200000})]
      (if number
        (do
          (db-in number)
          (log-in (session/get :user) number)
          (flash-suc "存款成功！" "/profile"))
        (flash-err "存款失败！可能是您输入无效。" "/profile")))))
(defn out [outvalue]
  (is-user
    (let [result (getuser (session/get :user))
          number (valid-num outvalue result)]
      (if number
        (do
          (db-out number)
          (log-out (session/get :user) number)
          (flash-suc "取款成功！" "/profile"))
        (flash-err "取款失败！可能是数字不正确或者无效输入。" "/profile")))))
(defn transger [touser value]
  (is-user
    (let [result (getuser (session/get :user))
          number (valid-num value result)]
      (if number
        (do
          (db-transfer touser number)
          (log-transfer (session/get :user) touser number)
          (flash-suc "转账成功！" "/profile"))
        (flash-err "转账失败！可能是你的输入有误，或者无效输入！" "/profile")))))
