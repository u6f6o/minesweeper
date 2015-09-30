(ns minesweeper.game
  (:require [minesweeper.board :as board]
            [minesweeper.dispatch :as disp]))


(def levels { :beginner     { :rows 8,  :cols 8,  :mines 10 }
              :intermediate { :rows 16, :cols 16, :mines 40 }
              :expert       { :rows 30, :cols 16, :mines 99 }})

(def ^:private level (atom {}))
(def ^:private board (atom []))


(defn- new-game
  [data]
  (let [new-level (:level data)]
    (reset! level new-level)
    (reset! board (board/empty-board
                   (:rows new-level)
                   (:cols new-level)))
    (disp/fire :game-initialized data)))


(defn- start-game
  [board mine-count start-pos]
  (-> board
      (board/place-mines mine-count start-pos)
      (board/place-warnings)))


(defn- explore
  [board data]
  (let [mine-count (:mines (:level data))
        position   (vector (:row data) (:col data))]
    (if (not (board/game-started? board))
      (-> board
          (start-game mine-count position)
          (board/explore-field position))
      (board/explore-field board position))))


(defn- explore-field
  [data]
  (let [board (swap! board explore data)
        attrs (get-in board (vector (:row data) (:col data)))
        data  (assoc data :attrs attrs)]
    (cond
     (board/game-won? board)   (disp/fire :game-won data)
     (board/game-lost? board)  (disp/fire :game-lost (assoc data :board board))
     :else                     (disp/fire :uncover-field data))))


(defn- toggle-flag
  [data]
  (let [position (vector (:row data) (:col data))]
    (swap! board (partial board/toggle-flag) position)
    (disp/fire :uncover-field data)))


(disp/register :explore-field #'explore-field)
(disp/register :toggle-flag #'toggle-flag)
(disp/register :new-game #'new-game)












