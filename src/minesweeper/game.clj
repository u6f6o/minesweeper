(ns minesweeper.game
   (:use [clojure.set]))

(defn empty-board
  [w h m]
  (let [empty-vec (vec (repeat (* w h) 0))]
    { :explored  empty-vec,
      :flags     empty-vec,
      :mines     empty-vec,
      :warnings  empty-vec,
      :width     w,
      :mine-cnt  m }))

(defn- size
  [board]
  (count (:mines board)))

(defn- width
  [board]
  (:width board))

(defn- height
  [board]
  (quot (size board) (width board)))

(defn dimension
  [board]
  (vector (width board) (height board)))

(defn mine-count
  [board]
  (:mine-cnt board))

(defn pos->idx
  [board pos]
  (let [[x y] pos]
    (+ x (* y (width board)))))

(defn- idx->pos
  [board idx]
  (let [w (width board)]
    (vector (mod idx w) (quot idx w))))

(defn- cell-modifier*
  [state modifier]
  (fn [board idx & xs]
    (update-in board [state idx] modifier xs)))

(def ^:private set-explored
  (cell-modifier* :explored (constantly 1)))

(def ^:private set-mine
  (cell-modifier* :mines (constantly 1)))

(def ^:private set-warning
  (cell-modifier* :warnings (fn [_ xs] (first xs))))

(def ^:private toggle-flag
  (cell-modifier* :flags (fn [x _] (* (dec x) -1))))

(defn won?
  [board]
  (let [ms (:mines board)
        es (:explored board)]
    (every? #(= % 1) (map + ms es))))

(defn lost?
  [board]
  (let [ms (:mines board)
        es (:explored board)]
    (some pos? (map * ms es))))

(defn started?
  [board]
  (some pos? (:mines board)))

(defn- gridder*
  [pattern]
  (fn [board idx]
    (let [[n m] (dimension board)
          pos   (idx->pos board idx)]
      (for [[x y] (map #(map + % pos) pattern)
            :when (and
                   (> n x -1)
                   (> m y -1))]
        (+ x (* y n))))))

(def ^:private cross-grid
  (vector [0 -1] [-1 0] [1 0] [0 1]))

(def ^:private square-grid
  (conj cross-grid [-1 -1] [-1 1] [1 -1] [1 1]))

(def ^:private neighbours
  (gridder* square-grid))

(defn- place-mines
  [board start-idx]
  (->> (range (size board))
       (remove #{start-idx})
       (shuffle)
       (take (mine-count board))
       (reduce set-mine board)))

(defn- place-warnings
  [board]
  (->> (keep-indexed #(if (pos? %2) %1) (:mines board))
       (mapcat (partial neighbours board))
       (frequencies)
       (reduce-kv set-warning board)))

(defn- nice-neighbours
  [board idx]
  (let [mines      (:mines board)
        explored   (:explored board)
        neighbours (neighbours board idx)
        is-set?    (fn [[k v]] (pos? v))]
    (when
      (not-any? is-set? (select-keys mines neighbours))
      (->> (select-keys explored neighbours)
           (remove is-set?)
           (keys)
           (into #{})))))

(defn- explore-cell
  ([board idx]
    (explore-cell board #{} #{idx}))
  ([board explored remaining]
   (if (empty? remaining)
     board
     (let [curr       (first remaining)
           explored   (conj explored curr)
           remaining  (disj remaining curr)
           board      (set-explored board curr)
           candidates (nice-neighbours board curr)
           remaining  (union remaining (difference candidates explored))]
       (recur board explored remaining)))))

(defn- init-game
  [board start-idx]
  (explore-cell
    (-> (place-mines board start-idx)
        (place-warnings))
    start-idx))

(defn init
  [board start-idx]
  (if-not (started? board)
    (init-game board start-idx)
    board))

(defn explore
  [board idx]
  (if-not (or (pos? (nth (:flags board) idx))
              (pos? (nth (:explored board) idx))
              (won? board)
              (lost? board))
    (explore-cell board idx)
    board))

(defn flag
  [board idx]
  (if-not (or (pos? (nth (:explored board) idx))
              (not (started? board))
              (won? board)
              (lost? board))
    (toggle-flag board idx)
    board))
