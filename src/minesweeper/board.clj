(ns minesweeper.board
  (:use [clojure.pprint]))



(defn index*
  [row-size]
  (fn [x y]
    (+ x (* y row-size))))

(defn coord*
  [row-size]
  (fn [idx]
    [(mod idx row-size) (quot idx row-size)]))

(defn size*
  [row-size]
  (fn [board]
    [row-size (quot (count board) row-size)]))


(def index-8 (index* 8))
(def coord-8 (coord* 8))
(def size-8 (size* 8))


(defn idx->coords
  [idx]
  (coord-8 idx))

(defn coords->idx
  [x y]
  (index-8 x y))

(defn board-size
  [board]
  (size-8 board))


;; TODO rename
(defn filter-cells
  [board pred]
  (keep-indexed
   (fn [i x] (when (pred x) i)) board))


(defn empty-board
  "Create an empty board with
  the specified width and height"
  [w h]
  (vec (repeat (* w h) {})))


(defn neighbour-cells
  "Locate neighbour cells based on coordinates [x y],
  respecting board width and height"
  [board idx]
  (let [[x y] (idx->coords idx)
        [w h] (board-size board)]
    (for [dx (map (partial + x) [-1 0 1])
          dy (map (partial + y) [-1 0 1])
          :when (and (or (not= x dx) (not= y dy))
                     (> w dx -1)
                     (> h dy -1))]
      (coords->idx dx dy))))


(defn warnings-freq
  [board]
  "Count the number of nearby mines"
  (let [mines    (filter-cells board :mine)
        warnings (mapcat (partial neighbour-cells board) mines)]
    (frequencies
      (remove (set mines) warnings))))


(defn random-mines
  [board mine-cnt start-pos]
  (take mine-cnt
        (-> (set (range (count board)))
            (disj start-pos)
            (shuffle))))


(defn place-mines
  "Place n mines randomly on the board"
  [board mine-count start-pos]
  (let [mines (random-mines board mine-count start-pos)]
    (reduce
     (fn [v i]
       (update v i conj {:mine true}))
     board
     mines)))


(defn place-warnings
  "Place warnings on a mines' neighbour cells"
  [board]
  (let [mine-counts (warnings-freq board)]
    (reduce-kv
     (fn [v i c]
       (update v i conj {:warn c}))
     board
     mine-counts)))


(defn explore-field
  "Explore single field on the board"
  [board coords]
  (let [idx (coords->idx coords)]
    (update board idx conj {:explored true})))


(defn toggle-flag
  "Sets and removes a flag"
  [board coords]
  (let [idx (coords->idx coords)]
    (update board
            idx
            #(assoc % :flag (not (:flag %))))))


(defn game-started?
  "At least one field explored?"
  [board]
  (some (partial :explored) board))


(defn game-lost?
  "Any mine exploded?"
  [board]
  (letfn [(pred [m] (and (:mine m) (:explored m)))]
    (some (partial pred) board)))


(defn game-won?
  "All fields cleared?"
  [board]
  (letfn [(pred [m] (or (:mine m) (:explored m)))]
    (every? (partial pred) board)))
