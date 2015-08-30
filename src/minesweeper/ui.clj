(ns minesweeper.ui
  (:gen-class)
  (:require [clojure.java.io :as io])
  (:use [seesaw core mig]
        [minesweeper game icons]))


(def started (atom false))
(def game (atom (vector nil)))
(def ui (frame :title "Minesweeper" :on-close :exit))
(def mine-count (atom nil))


(def levels { :beginner     { :rows 8,  :cols 8,  :mines 10 }
              :intermediate { :rows 16, :cols 16, :mines 40 }
              :expert       { :rows 16, :cols 30, :mines 99 }})



(defn select-field
  [row col]
  (select ui [(keyword (str "#field_" row "_" col))]))


(defn choose-icon
  [field-attrs]
  (println field-attrs)
  (cond
   (and (:mine field-attrs) (:flag field-attrs)) (cell-icons :redmine)
   (:mine field-attrs)                           (cell-icons :mine)
   (:warn field-attrs)                           (cell-icons (keyword (str (:warn field-attrs))))
   :else                                         (cell-icons :0)))


(defn expose-field
  [row col]
  (let [field (select-field row col)
        field-attrs (get-in @game [row col])]
    (config! field :icon (choose-icon field-attrs))))


(defn game-won
  []
  (println "YEAH"))


(defn game-lost
  []
  (let [coords (to-coords @game)]
    (doseq [pos coords]
      (expose-field (first pos) (second pos)))
    pack! ui))


(defn examine-field
  [row col]
  (do
    (when (not @started)
      (reset! game
              (place-warnings
               (place-mines @game @mine-count [row col])))
      (reset! started true))
    (swap! game #(clear-field % [row col]))
    (cond
     (game-won? @game) (println "jgewoejgew")
     (game-lost? @game) (game-lost)
     :else (expose-field row col))))



(defn make-button [row col]
    (button :id (str "field_" row "_" col)
            :icon (cell-icons :button)
            :listen [:action
                    (fn [e] (examine-field row col))]))


(defn make-board
  [row col]
  (mig-panel
   :constraints [(str "gap 0, wrap" row) "[]" "[]" ]
   :items (for [x (range row) y (range col)]
            (vector (make-button x y) "w 24px!, h 24px!"))))


(defn make-ui
  [rows cols]
  (do
    (config! ui :content (make-board rows cols))
    (pack! ui)
    (config! ui :resizable? false)
    (show! ui)))


(defn new-game
  ([]
   (new-game (:beginner levels)))
  ([level]
   (new-game (:rows level)
             (:cols level)
             (:mines level)))
  ([rows cols mines]
   (do
     (reset! started false)
     (reset! game (empty-board rows cols))
     (reset! mine-count mines)
     (make-ui rows cols))))


(defn -main
  []
  (do
    (native!)
    (new-game)))
