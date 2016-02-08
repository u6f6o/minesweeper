(defproject minesweeper "2.0.0"
  :description "Minesweeper game for learning purposes"
  :url "https://github.com/u6f6o/minesweeper"
  :license {:name "The MIT License (MIT)"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [seesaw "1.4.5"]]
  :main ^:skip-aot minesweeper.repl
  :target-path "target/%s"
  :profiles {:main-ui {:main ^:skip-aot minesweeper.ui }
             :uberjar {:aot :all}}
  :aliases {"run" ["with-profile" "main-ui" "run"]})
