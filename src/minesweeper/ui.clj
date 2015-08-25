(ns minesweeper.ui
  (:gen-class)
  (:require [clojure.java.io :as io])
  (:use [seesaw core mig]
        [minesweeper core game file]))


;; (def board (atom (init-game 5 5 10 [3 3])))


;; (defn init-icons
;;   []
;;   (let [fs (list-dir (res-as-file "minesweeper/icons/"))]
;;     (reduce
;;      #(assoc %1 (keyword (basename %2)) (icon %2))
;;      {}
;;      fs)))

;; (def icons (init-icons))

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



(defn make-button [x y]
    (button :id (format "cell_%d_%d" x y)
            :icon (clojure.java.io/resource "minesweeper/icons/button.png")))
;;             :listen [:action
;;                     (fn [e] (click-button row col))]))

(defn make-board
  [w h]
  (mig-panel
   :constraints [(str "wrap" w) "[]" "[]" ]
   :items (for [x (range w) y (range h)]
            (vector (make-button x y) "w 24px!, h 24px!"))))






 (defn make-frame
   []
     (frame :title      "Minesweeper"
            :width      250 ;;(* 24 w)
            :height     500 ;;(* 24 h)
            :on-close   :exit
            :content    (make-board 5 5)))




 (defn foobar []
   (native!)
  (-> (make-frame)
      show!))

(foobar)





