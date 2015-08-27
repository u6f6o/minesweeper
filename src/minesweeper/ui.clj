(ns minesweeper.ui
  (:gen-class)
  (:require [clojure.java.io :as io])
  (:use [seesaw core mig]
        [minesweeper game file]))


(defn init-icons
  []
  (let [fs (list-dir (res-as-file "minesweeper/icons/"))]
    (reduce
     #(assoc %1 (keyword (basename %2)) (icon %2))
     {}
     fs)))

(defn make-frame
   []
     (frame :title      "Minesweeper"
            :width      250 ;;(* 24 w)
            :height     250 ;;(* 24 h)
            :on-close   :exit))

(def root (make-frame))
(def board (atom (init-game 16 16 40 [3 3])))
(def icons (init-icons))


(defn select-field
  [row col]
  (select root [(keyword (str "#field_" row "_" col))]))

(defn choose-icon
  [field-attrs]
  (println field-attrs)
  (cond
   (:mine field-attrs) (:mine icons)
   (:warn field-attrs) ((keyword (str (:warn field-attrs))) icons)
   :else (:0 icons)))

(defn expose-field
  [row col]
  (let [field (select-field row col)
        field-attrs (get-in @board [row col])]
    (config! field :icon (choose-icon field-attrs))))



(defn game-won
  []
  (println "YEAH"))

(defn game-lost
  []
  (let [coords (to-coords @board)]
    (doseq [pos coords]
      (expose-field (first pos) (second pos)))
    pack!))


(defn examine-field
  [row col]
  (do
    (println board)
    (swap! board #(clear-field % [row col]))
    (cond
     (game-won? @board) (println "jgewoejgew")
     (game-lost? @board) (game-lost)
     :else (expose-field row col))))


(defn make-button [row col]
    (button :id (str "field_" row "_" col)
            :icon (:button icons)
            :listen [:action
                    (fn [e] (examine-field row col))]))

(defn make-board
  [row col]
  (mig-panel
   :constraints [(str "wrap" row) "[]" "[]" ]
   :items (for [x (range row) y (range col)]
            (vector (make-button x y) "w 24px!, h 24px!"))))


(config! root :content (make-board 16 16))

(defn -main
  []
  (native!)
  (show! root))
