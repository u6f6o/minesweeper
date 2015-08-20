(ns minesweeper.game
  (:use [clojure.pprint]))


(defn- empty-board
  "Create a rectangular empty board of the specified with and height"
  [w h]
  (vec (repeat w (vec (repeat h nil)))))


(defn to-coords
  ([board]
   (to-coords board (constantly true)))
  ([board pred]
   (let [w (count board)
         h (count (first board))]
     (for [x (range w) y (range h) :when (pred (get-in board [x y]))]
       [x y]))))


(defn board->coords
  "Coordinates of the board"
  [board]
  (to-coords board))


(defn mines->coords
  "Coordinates of the mines on the board"
  [board]
  (to-coords board :mine))


(defn neighbours
  "Locate neighbour cells based on coordinates [x y],
  respecting board width and height"
  [board [x y]]
  (let [w (count board)
        h (count (first board))]
    (for [dx (map #(+ % x) [-1 0 1])
          dy (map #(+ % y) [-1 0 1])
          :when (and (or (not= x dx) (not= y dy))
                     (> w dx -1)
                     (> h dy -1))]
      [dx dy])))


(defn place-mines
  "Place n mines randomly on the board"
  [board n]
  (let [mines (take n (shuffle (to-coords board)))]
    (reduce
     (fn [board coordinates]
       (assoc-in board coordinates {:mine true}))
     board
     mines)))


(defn place-warnings
  "Place warnings on a mines' neighbour cells"
  [board]
  (let [ws (->> board
                mines->coords
                (mapcat (partial neighbours board))
                (remove #(get-in board (conj % :mine))))]
    (reduce
     (fn [board coordinates]
       (update-in board (conj coordinates :warn) (fnil inc 0)))
     board
     ws)))


(defn init-game
  "Create board and place mines and warnings"
  [width height mine-count]
  (-> (empty-board width height)
      (place-mines mine-count)
      (place-warnings)))


(defn mine-triggered?
  [board]
  (letfn [(pred [m] (every? (or m {}) [:mine :flag]))]
    (not-empty
      (to-coords board pred))))


(defn board-cleared?
  [board]
  (letfn [(pred [m] (not-any? (or m {}) [:mine :flag]))]
    (empty?
     (to-coords board pred))))




