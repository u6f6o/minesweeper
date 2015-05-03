(ns minesweeper.game
  (:use [clojure.pprint]))


(defn empty-board
  "Creates a rectangular empty board of the specified with and height"
  [w h]
  (vec (repeat w (vec (repeat h nil)))))

(defn neighbours
  "Locate a given cells' neighbour cells"
  [[x y]]
  (for [dx [-1 0 1 ] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

(defn locate-mines
  "Locate mines coordinates on the board"
  [board]
  (for [[x row] (map-indexed vector board)
        [y val] (map-indexed vector row)
        :when (:mine val)]
    [x y]))

(defn locate-warnings
  "Locate warnings coordinates on the board"
  [board]
  (let [mines (locate-mines board)]
    (remove #(get-in board %) (mapcat neighbours mines))))

(defn place-mines
  "Place mines on the board"
  [board mines]
  (reduce
    (fn [board coordinates]
      (assoc-in board coordinates {:mine true}))
   board
   mines))

(defn place-warnings
  [board]
  (let [warnings (locate-warnings board)]
    (reduce
      (fn [board coordinates]
        (update-in board (conj coordinates :warn) (fnil inc 0)))
     board
     warnings)))

(defn populate
  "Places a mine on each of the cells specified as [x, y] coordinates"
  [board mined-cells]
  (place-warnings (place-mines board mined-cells)))


(def glider (populate (empty-board 10 10) #{[1 1] [1 2]}))
(pprint  glider)
