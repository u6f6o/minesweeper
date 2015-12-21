(defproject minesweeper "1.0.0"
  :description "Minesweeper game for learning purposes"
  :url "https://github.com/u6f6o/minesweeper"
  :license {:name "The MIT License (MIT)"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :main ^:skip-aot minesweeper.main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
