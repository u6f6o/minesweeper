(ns minesweeper.game
  (:use [clojure.pprint]))


(defn empty-board
  "Create a rectangular empty board of
  the specified with and height"
  [w h]
  (vec (repeat w (vec (repeat h {})))))


(defn to-coords
  "Transform the board cells into coordinates"
  ([board]
   (to-coords board (constantly true)))
  ([board pred]
   (let [w (count board)
         h (count (first board))]
     (for [x (range w) y (range h) :when (pred (get-in board [x y]))]
       [x y]))))


(defn neighbour-cells
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


(defn warnings-freq [board]
  "Count the number of nearby mines"
  (let [mines (to-coords board :mine)
        warnings (mapcat (partial neighbour-cells board) mines)]
    (frequencies
     (remove (set mines) warnings))))


(defn random-mines
  [board start-pos]
  (-> (set (to-coords board))
      (disj start-pos)
      (shuffle)))


(defn place-mines
  "Place n mines randomly on the board"
  [board mine-count start-pos]
  (let [mines (take mine-count
                    (random-mines board start-pos))]
    (reduce
     (fn [m k]
       (assoc-in m k {:mine true}))
     board
     mines)))


(defn place-warnings
  "Place warnings on a mines' neighbour cells"
  [board]
  (let [mine-counts (warnings-freq board)]
    (reduce-kv
     (fn [m k v]
       (assoc-in m k {:warn v}))
     board
     mine-counts)))


(defn explore-field
  "Explore single field on the board"
  [board coords]
  (update-in board coords conj {:explored true}))


(defn handle-flag
  "Handles set and remove of a flag"
  [board coords]
  (update-in board coords
             #(assoc % :flag (not (:flag %)))))


(defn init-game
  "Create board and place mines and warnings"
  [w h mine-count start-pos]
  (-> (empty-board w h)
      (explore-field start-pos)
      (place-mines mine-count start-pos)
      (place-warnings)))


(defn game-lost?
  "Any mine exploded?"
  [board]
  (letfn [(pred [m] (and (:mine m) (:explored m)))]
    (pos? (count (to-coords board pred)))))


(defn game-won?
  "All fields cleared?"
  [board]
  (letfn [(pred [m] (or (:mine m) (:explored m)))]
    (= (count (to-coords board pred))
       (count (to-coords board)))))
