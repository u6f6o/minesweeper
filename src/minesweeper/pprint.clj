(ns minesweeper.pprint
  (:use [clojure.pprint]))

(defn- cell-desc
  [cell]
  (if (:mine cell)
    (symbol "X")
    (or (:warn cell)
      0 )))

(defn print-board
  [board]
  (pprint
   (map
    (fn [row]
      (map cell-desc row))
    board)))


