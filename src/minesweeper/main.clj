(ns minesweeper.main
  (:require [minesweeper.game :as game]
            [minesweeper.display :as display]))

(def game (atom {}))


(defn setup-game
  []
  (-> (game/empty-board 4 4 3)
      (game/place-mine [0 0])
      (game/place-mine [1 1])
      (game/place-mine [3 3])
      (game/place-warning [0 1] 2)
      (game/place-warning [0 2] 1)
      (game/place-warning [1 0] 2)
      (game/place-warning [1 2] 1)
      (game/place-warning [2 0] 1)
      (game/place-warning [2 1] 1)
      (game/place-warning [2 2] 2)
      (game/place-warning [2 3] 1)
      (game/place-warning [3 2] 1)))


(defn start-game
  []
  (reset! game (setup-game))
  nil)


(defn next-turn
  [x y]
  (swap! game game/explore-cell [x y])
  nil)


(defn won?
  []
  (game/won? @game))


(defn lost?
  []
  (boolean (game/lost? @game)))


(defn show-board
  []
  (display/display-board @game))
