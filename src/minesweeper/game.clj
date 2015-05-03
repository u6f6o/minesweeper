(ns minesweeper.game
  (:use [clojure.pprint]))


(defn empty-board
  "Creates a rectangular empty board of the specified with and height"
  [w h]
  (vec (repeat w (vec (repeat h nil)))))

(defn populate
  "Places a mine on each of the cells specified as [x, y] coordinates"
  [board mined-cells]
  (reduce (fn [board coordinates]
            (assoc-in board coordinates {:mine true}))
          board
          mined-cells))

(defn locate-mines
  "Locate mines on the board"
  [board]
  (for [[x row] (map-indexed vector board)
        [y val] (map-indexed vector row)
        :when (:mine val)]
    [x y]))

(defn neighbours
  "Locate a given cells' neighbour cells"
  [[x y]]
  (for [dx [-1 0 1 ] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

(defn place-warnings
  [board]
  (let [mines (locate-mines board)
        warnings (mapcat neighbours mines)]
    (reduce
     (fn [board coordinates]
       (update-in board (conj coordinates :warn) (fnil inc 0)))
     board
     warnings)))


(def bar [{:mine 4}])
(update-in bar [0 :mine] (fnil inc 0))

(def glider (populate (empty-board 10 10) #{[1 1] [1 2]}))
(pprint (place-warnings glider))
