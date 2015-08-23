(ns minesweeper.ui
  (:gen-class)
  (:require [clojure.java.io :as io])
  (:use [seesaw core mig]
        [minesweeper core game util]))


(def board (atom (init-game 5 5 10 [3 3])))

;; (defn make-button [row col]
;;   (button :text (format "(%d, %d)" row col)
;;           :listen [:action
;;                    (fn [e] (alert (format "Hi, you clicked on (%d, %d)!" row col)))]))


(defn make-frame
  [board]
  (frame :title       "Minesweeper"
         :width       (* 32 (count (board)))
         :height      (* 32 (count (first (board))))
         :on-close    :exit))


;; (def icons {
;;             :default   (icon (clojure.java.io/resource "minesweeper/icons/default.png"))
;;             :1         (icon (clojure.java.io/resource "minesweeper/icons/1.png"))
;;             :2         (icon (clojure.java.io/resource "minesweeper/icons/1.png"))
;;             :3         (icon (clojure.java.io/resource "minesweeper/icons/1.png"))
;;             :1         (icon (clojure.java.io/resource "minesweeper/icons/1.png"))
;;             :1         (icon (clojure.java.io/resource "minesweeper/icons/1.png"))
;;             :1         (icon (clojure.java.io/resource "minesweeper/icons/1.png"))
;;             :1         (icon (clojure.java.io/resource "minesweeper/icons/1.png"))
;;             :1         (icon (clojure.java.io/resource "minesweeper/icons/1.png"))
;;             :1         (icon (clojure.java.io/resource "minesweeper/icons/1.png"))
;;             :1         (icon (clojure.java.io/resource "minesweeper/icons/1.png"))
;;             :1         (icon (clojure.java.io/resource "minesweeper/icons/1.png"))
;;             :1         (icon (clojure.java.io/resource "minesweeper/icons/1.png"))

;;             })


(defn init-icons
  []
  (let [fs (->> "minesweeper/icons/"
                io/resource
                io/file
                file-seq)]
    (reduce #(assoc %1 (.getName %2) %2) {} fs)))




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


;; (defn foobar []
;;   (native!)
;;   (-> (frame :title "Minesweeper"
;;              :content (make-content)
;;              :width 350
;;              :height 350
;;              :on-close :exit)
;;       show!))

;; (foobar)
(init-icons)
