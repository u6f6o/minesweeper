(ns minesweeper.board
  (:use [clojure.pprint]))


(def levels { :beginner     { :w 8  :h 8  :m 10 }
              :intermediate { :w 16 :h 16 :m 40 }
              :expert       { :w 30 :h 16 :m 99 }})


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

(defn- every-state
  [xs coll-f]
  (let [cs (combine-states xs)]
    (fn [coll]
      (coll-f #(= cs (bit-and % cs)) coll))))

(defn- any-state
  [xs coll-f]
  (let [cs (combine-states xs)]
    (fn [coll]
      (coll-f #(pos? (bit-and % cs)) coll))))

(defn- board->meta
  ([b]
   (let [meta-bit (first b)
         trans    (fn [x] (state->bit (key x)))
         pred     (fn [x] (= x (bit-and x meta-bit)))]
     (val
      (first
       (filter (comp pred trans) levels)))))
  ([b & ks]
   (let [bm (board->meta b)]
     (if (> (count ks) 1)
       (vec (vals (select-keys bm ks)))
       (get bm (first ks))))))

(defn- pos->idx
  [b [x y]]
  (+ x (* y (board->meta b :w))))

(defn- idx->pos
  [b i]
  (let [row-size (board->meta b :w)]
    (vector
     (mod i row-size)
     (quot i row-size))))



(defn empty-board
  "Create an empty board with
  the specified width and height"
  [level]
  (let [meta-bit (state->bit level)
        [w h _]  (get levels level)]
    (into [meta-bit]
          (vec (repeat (dec (* w h)) 0)))))


(def cells-w-mines
  (every-state [:mine] filter))

(def game-started?
  (every-state [:explored] some))

(def game-lost?
  (every-state [:explored :mine] some))

(def game-won?
  (any-state [:explored :mine] every?))


(defn neighbour-cells
  "Locate neighbour cells based on coordinates [x y],
  respecting board width and height"
  [board idx]
  (let [[x y] (idx->pos board idx)
        [w h] (board->meta board :w :h)]
    (for [dx (map (partial + x) [-1 0 1])
          dy (map (partial + y) [-1 0 1])
          :when (and (or (not= x dx) (not= y dy))
                     (> w dx -1)
                     (> h dy -1))]
      (pos->idx board [dx dy]))))


(defn find-warnings
  [board mines]
  (mapcat
   (partial neighbour-cells board) mines))


(defn warnings-freq
  [board]
  "Count the number of nearby mines"
  (let [mines    (cells-w-mines board)
        warnings (find-warnings board mines)]
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
  (let [idx (pos->idx coords)]
    (update board idx conj {:explored true})))


(defn toggle-flag
  "Sets and removes a flag"
  [board coords]
  (let [idx (pos->idx coords)]
    (update board
            idx
            #(assoc % :flag (not (:flag %))))))


