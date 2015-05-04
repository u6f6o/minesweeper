(ns minesweeper.core
  (:use [minesweeper.game]
        [minesweeper.pprint]
        [clojure.pprint])
  (:gen-class))


(defn -main
  "Initialize board and print it."
  [& args]
  (print-board (init-game 10 10 25)))
