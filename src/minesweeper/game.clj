(ns minesweeper.game
  (:require [minesweeper.board :as board]
            [minesweeper.dispatch :as disp]))


(def board (atom (vec (repeat 8 (vec (repeat 8 {}))))))



(defn init-game
  [board mine-count start-pos]
  (-> board
      (board/place-mines mine-count start-pos)
      (board/place-warnings)))


(defn explore
  [board data]
  (let [mine-count (:mine-count data)
        position   (vector (:row data) (:col data))]
    (if (not (board/game-started? board))
      (-> board
          (init-game mine-count position)
          (board/explore-field position))
      (board/explore-field board position))))


(defn explore-field
  [data]
  (do
    (let [board (swap! board explore data)
          attrs (get-in board (vector (:row data) (:col data)))]
      (cond
       (board/game-won? board)   (disp/fire :game-won data)
       (board/game-lost? board)  (disp/fire :game-lost data)
       :else                     (disp/fire :uncover-field (assoc data :attrs attrs))))))



(disp/register :explore-field #'explore-field)













