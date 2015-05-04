(ns minesweeper.core
  (:use [minesweeper.game]
        [clojure.pprint])
  (:gen-class))


(init-game 10 10 25)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
