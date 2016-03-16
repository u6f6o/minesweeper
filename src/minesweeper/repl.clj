(ns minesweeper.repl
  (:require [minesweeper.game :as game]
            [minesweeper.display :as display]))

(defn- empty-board
  [w h m]
  (game/empty-board w h m))

(defn- coordinator*
  [game-fc]
  (fn [board [x y]]
    (game-fc board (game/pos->idx board [x y]))))

(def init (coordinator* game/init))
(def explore (coordinator* game/explore))
(def flag (coordinator* game/flag))

(defn display
  [board]
  (display/display-board board))
