(defproject minesweeper "0.1.0-SNAPSHOT"
  :description "Minesweeper game for learning purposes"
  :url "https://github.com/u6f6o/minesweeper"
  :license {:name "The MIT License (MIT)"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [seesaw "1.4.5"]]
  :main ^:skip-aot minesweeper.ui
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
