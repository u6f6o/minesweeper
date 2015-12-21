(ns minesweeper.game)


(defn empty-board
  [w h m]
  (let [empty-vec (vec (repeat (* w h) 0))]
    { :explored  empty-vec,
      :flags     empty-vec,
      :mines     empty-vec,
      :warnings  empty-vec,
      :width     w,
      :mine-cnt  m }))


(defn- board-with
  [board]
  (:width board))


(defn- pos->idx
  [board pos]
  (let [[x y] pos]
    (+ x (* y (board-with board)))))


(defn- cell-modifier*
  [state modifier]
  (fn [board pos & xs]
    (let [idx (pos->idx board pos)]
      (update-in board [state idx] modifier xs))))


(def explore-cell
  (cell-modifier* :explored (constantly 1)))


(def place-mine
  (cell-modifier* :mines (constantly 1)))


(def place-warning
  (cell-modifier* :warnings (fn [_ xs] (first xs))))


(def toggle-flag
  (cell-modifier* :flags (fn [x _] (* (- x 1) -1))))


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
