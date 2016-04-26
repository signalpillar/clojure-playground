(ns shoppingcard.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defonce database (atom {:max-id 0
                     :items []}))

(defn add-item [item]
  (swap! database (fn [state]
                    (let [id (inc (:max-id state))
                          state (conj state [:max-id id])
                          updated-list (conj (:items state) {:id id, :item item})
                          updated-state (conj state [:items updated-list])]
                      updated-state))))

(defn find-item [idx]
  (let [items (:items @database)]
    (first (filter (fn [item] (= idx (str (:id item)))) items))))

(defn list-items []
  (->> @database
  (:items)
  (clojure.string/join "<br>")))

(defn delete-item [idx]
  (fn [item] (= idx (str (:id item))))
  (:items @database))

(defroutes app
  (GET "/carts/" [] (str (list-items)))
  (GET "/carts/:id" [id] (str "Item: " (find-item id)))
  (GET "/state" [] (pr-str @database))
  (POST "/carts/" [] (do (add-item "foobar") "ok"))
  (route/not-found "Not Found"))
