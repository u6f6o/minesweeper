(ns minesweeper.ui
  (:gen-class)
  (:require [clojure.java.io :as io])
  (:use [seesaw core mig]
        [minesweeper core game file]))


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
(def board (atom (init-game 5 5 10 [3 3])))
(def icons (init-icons))



;; (defn make-buttons
;;   [w h]
;;   (vec
;;    (for [x (range w) y (range h)]
;;      (button :text ""
;;              :preferred-size [24 :by 24]
;;              :class :game-button
;;              :icon (:button icons)
;;              :id (str "cell_" x "_" y)))))


;; (defn make-board [w h]
;;   (let [buttons (make-buttons w h)]
;;     (grid-panel
;;      :id :board
;;      :hgap 0
;;      :vgap 0
;;      :rows w
;;      :columns h
;;      :preferred-size [(* w 24) :by (* h 24)]
;;      :items buttons)))


;; (defn make-border-panel
;;   [w h]
;;   (border-panel
;;    :border 5
;;    :hgap 5
;;    :vgap 5
;;    :north (label
;;            :h-text-position
;;            :center
;;            :text
;;            "Welcome to minesweeper")
;;    :center (make-board w h)
;;    :south (label
;;            :h-text-position
;;            :center
;;            :text
;;            "Ready to play!")))


;; (defn make-frame
;;   [board]
;;   (let [w (count board)
;;         h (count (first board))]
;;     (frame :title      "Minesweeper"
;;            :width      250 ;;(* 24 w)
;;            :height     500 ;;(* 24 h)
;;            :on-close   :exit
;;            :content    (make-border-panel w h))))







;; (defn click-button
;;   [row col]
;;   (do
;;     (swap! board #(clear-field % [row col]))
;;     (println @board)
;;     (repaint! (id-of "#miefield"))))


;; (defn make-button [row col]
;;    ;;(button :text (format "(%d, %d)" row col)
;;     (button :id (format "field_%d_%d" row col)
;;             :icon (clojure.java.io/resource "minesweeper/icons/button.png")
;;             :listen [:action
;;                     (fn [e] (click-button row col))]))

;; (defn make-content []
;;   (mig-panel
;;    :id "minefield"
;;    :constraints ["fill"]
;;    :items
;;    (let [rows 5 cols 5]
;;      (for [row (range rows) col (range cols)]
;;        [(make-button row col)
;;         (if (and (< row (dec rows)) (= col (dec cols))) "grow, wrap" "grow")]))))



;; (defn make-border-panel
;;   []
;;   (mig-panel
;;     :constraints ["wrap 3"                             ;; Layout
;;                   "[shrink 0][shrink 0]20px[200, grow, fill]"    ;; Column
;;                   "[shrink 0]5px[]"]                   ;; Row
;;     :items [ ["name:"     ] [(text (or "Ulf"     ""))]
;;              ["category:" ] [(text (or "Programmierer" ""))]
;;              ["date:"     ] [(text (or "20.12.2012"     ""))]
;;              ["comment:"  ] [(text (or "Exzellenter Softwareentwickler!" ""))]]))

(defn select-field
  [row col]
  (select root [(keyword (str "#field_" row "_" col))]))

(defn choose-icon
  [field-attrs]
  (cond
   (:mine field-attrs) (:mine icons)
   (:flag field-attrs) ((keyword (str (or (:warn field-attrs) 0))) icons)
   :else (:button icons)))

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
  (println "FUCK"))

(defn examine-field
  [row col]
  (do
    (println board)
    (swap! board #(clear-field % [row col]))
    (cond
     (game-won? @board) (println "jgewoejgew")
     (game-lost? @board) (println "kglwjelglw")
     :else (expose-field row col))))


(defn make-button [row col]
    (button :id (str "field_" row "_" col)
            :icon (:button icons)
            :listen [:action
                    (fn [e] (examine-field row col))]))

(defn make-board
  [w h]
  (mig-panel
   :constraints [(str "wrap" w) "[]" "[]" ]
   :items (for [x (range w) y (range h)]
            (vector (make-button x y) "w 24px!, h 24px!"))))


(config! root :content (make-board 5 5))

(defn foobar []
   (native!)
    (show! root))

(do
  (println @board)
  (foobar))





