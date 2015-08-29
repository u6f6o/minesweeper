(ns minesweeper.game-test
  (:require [clojure.test :refer :all]
            [minesweeper.game :refer :all]))


(deftest test-win-condition
  (testing "All fields cleared"
    (let [board [[{:flag true} {:flag true} {:flag true}]
                 [{:flag true} {:mine true} {:flag true}]
                 [{:flag true} {:flag true} {:flag true}]]]
      (is (game-won? board))))
  (testing "Single covered field left"
    (let [board [[{:flag true} {:flag true} {:flag true}]
                 [{:flag true} {:mine true} {:flag true}]
                 [{:flag true} {:flag true} {}]]]
      (is (not (game-won? board)))))
  (testing "No field uncovered"
    (let [board [[{} {}           {}]
                 [{} {:mine true} {}]
                 [{} {}           {}]]]
      (is (not (game-won? board))))))




