(ns minesweeper.board
  (:use [clojure.pprint]))


(def levels { :beginner     [8  8  10]
              :intermediate [16 16 40]
              :expert       [30 16 99] })


(def states { :mine         2r00000000000001
              :explored     2r00000000000010
              :flag         2r00000000000100
              :beginner     2r00000000001000
              :intermediate 2r00000000010000
              :expert       2r00000000100000
              1             2r00000001000000
              2             2r00000010000000
              3             2r00000100000000
              4             2r00001000000000
              5             2r00010000000000
              6             2r00100000000000
              7             2r01000000000000
              8             2r10000000000000 })


(defn- state->bit
  [x]
  (get states x))

(defn- combine-states
  [xs]
  (apply bit-or (conj (map state->bit xs) 0)))

(defn- any-with-all?
  [& s]
  (let [cs (combine-states s)]
    (fn [coll]
      (some #(= cs (bit-and % cs)) coll))))

(defn- all-with-any?
  [& s]
  (let [cs (combine-states s)]
    (fn [coll]
      (every? #(pos? (bit-and % cs)) coll))))

(defn- board->meta
  [b]
  (let [meta-bit (first b)
        trans    (fn [x] (state->bit (key x)))
        pred     (fn [x] (= x (bit-and x meta-bit)))]
    (first (filter (comp pred trans) levels))))

(defn- row-size
  [b]
  (first (val (board->meta b))))

(defn- pos->idx
  [b [x y]]
    (+ x (* y (row-size b))))

(defn- idx->pos
  [b i]
    (vector
     (mod i (row-size b))
     (quot i (row-size b))))



(defn empty-board
  "Create an empty board with
  the specified width and height"
  [level]
  (let [meta-bit (state->bit level)
        [w h _]  (get levels level)]
    (into [meta-bit]
          (vec (repeat (dec (* w h)) 0)))))


;;    (vec (repeat (* w h) meta-bit))))
    ;;(println (str meta-bit w h))))
    ;;(vec (repeat (* w h) meta-bit))))

(state->bit :beginner)
(count (empty-board :beginner))


(defn neighbour-cells
  "Locate neighbour cells based on coordinates [x y],
  respecting board width and height"
  [board idx]
  (let [[x y] (idx->pos idx)
        [w h] (board-size board)]
    (for [dx (map (partial + x) [-1 0 1])
          dy (map (partial + y) [-1 0 1])
          :when (and (or (not= x dx) (not= y dy))
                     (> w dx -1)
                     (> h dy -1))]
      (pos->idx dx dy))))


(defn warnings-freq
  [board]
  "Count the number of nearby mines"
  (let [mines    (filter (partial bit-and 2r1) board)
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

(defn


(defn explore-field
  "Explore single field on the board"
  [board coords]
  (let [idx (pos->idx coords)]
    (update board idx conj {:explored true})))


(defn toggle-flag
  "Sets and removes a flag"
  [board coords]
  (let [idx (pos->idx coords)]
    (update board
            idx
            #(assoc % :flag (not (:flag %))))))


(def game-started?
  (any-with-all? :explored))

(def game-lost?
  (any-with-all? :explored :mine))

(def game-won?
  (all-with-any? :explored :mine))
