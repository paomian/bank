(ns bank.template
  (:use [hiccup core page]
        [hiccup.form])
  (:require [noir.session              :as session]))

(defn html-temp [code]
  (let [user (session/get :user)]
    (html5
      [:head
       [:title "银行管理系统"]
       (include-css "/bootstrap/css/bootstrap.min.css")
       (include-css "/bootstrap/css/bootstrap-responsive.min.css")
       (include-css "/bootstrap/css/my.css")
       ]
      [:body
       [:div.navbar.navbar-inverse 
        [:div.navbar-inner 
         [:div.container 
          [:a.btn.btn-navbar {:data-toggle "collapse" :data-target ".nav-collapse"}
           (for [x (range 4)] 
             [:span.icon-bar])]
          [:a.brand {:href "/"} "Bank"]
          [:ul.nav 
           [:li [:a {:href "/"} "主页"]]
           [:li [:a {:href "http://sdutlinux.org/"} "技术支持"]]
           [:li [:a {:href "http://gotit.asia/"} "got it"]]
           ]
          [:div.nav-collapse.collapse
           [:div.btn-group.pull-right
            [:button.btn.dropdown-toggle.btn-primary {:data-toggle "dropdown" :href "#"} (if user (str user) "用户")
             [:span.caret]]
            (if user 
              [:ul.dropdown-menu
               [:li [:a {:href "/logout"} "注销"]]]
              [:ul.dropdown-menu
               [:li [:a {:href "/login"} "登陆"]]])
            ]]]]]
       code
       (include-js "/bootstrap/js/jquery-2.0.3.min.js")
       (include-js "/bootstrap/js/bootstrap.min.js")
       ])))
(defmacro template [page-name [& args] & code]
  `(defn ~page-name [~@args]
     (html-temp (do ~@code))))
#_(defmacro def-page [page-name [& args] & code]
    `(defn ~page-name [~@args]
       (html-doc (do ~@code))))
