(ns minesweeper.core
  (:require [clojure.string :as str])
  (:use [minesweeper.game]
        [minesweeper.io])
  (:gen-class))

;; (def board (atom (init-game 5 5 10 [3 3])))


;; (defn read-coords
;;   []
;;   (map #(Integer/parseInt %) (str/split (read-line) #",")))


;; (defn play
;;   []
;;   (while
;;     (not (or (game-won? @board) (game-lost? @board)))
;;     (do
;;       (print-board @board)
;;       (println "Please provide coordinates 'x,y': ")
;;       (swap!
;;        board
;;        #(clear-field % (read-coords)))
;;       (println @board))))


;; (defn -main
;;   "Initialize board and print it."
;;   [& args]
;;   (play))


