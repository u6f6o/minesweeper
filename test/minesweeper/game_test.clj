(ns minesweeper.game-test
  (:require [clojure.test :refer :all]
            [minesweeper.game :refer :all]))


(deftest test-win-conditions
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
      (is (not (game-won? board)))))
  (testing "Empty board"
    (let [board [[{} {} {}]
                 [{} {} {}]
                 [{} {} {}]]]
      (is (not (game-won? board))))))


(deftest test-lose-conditions
  (testing "Uncover mined field"
    (let [board [[{} {}                       {}]
                 [{} {:mine true, :flag true} {}]
                 [{} {}                       {}]]]
      (is (game-lost? board))))
  (testing "All fields cleared"
    (let [board [[{:flag true} {:flag true} {:flag true}]
                 [{:flag true} {:mine true} {:flag true}]
                 [{:flag true} {:flag true} {:flag true}]]]
      (is (not (game-lost? board)))))
  (testing "No field uncovered"
    (let [board [[{} {}           {}]
                 [{} {:mine true} {}]
                 [{} {}           {}]]]
      (is (not (game-lost? board)))))
  (testing "Empty board"
    (let [board [[{} {} {}]
                 [{} {} {}]
                 [{} {} {}]]]
      (is (not (game-lost? board))))))


(deftest test-neighbour-cells
  (testing "Get neighbour cells from center"
    (let [cell             [1 1]
          board            [[{} {} {}]
                            [{} {} {}]
                            [{} {} {}]]
          exp-neighbours   [[0 0] [0 1] [0 2]
                            [1 0]       [1 2]
                            [2 0] [2 1] [2 2]]]
      (is (= exp-neighbours (neighbour-cells board cell)))))
  (testing "Get neighbour cells from border"
    (let [cell             [1 0]
          board            [[{} {} {}]
                            [{} {} {}]
                            [{} {} {}]]
          exp-neighbours   [[0 0] [0 1]
                                  [1 1]
                            [2 0] [2 1]]]
      (is (= exp-neighbours (neighbour-cells board cell)))))

  (testing "Get neighbour cells from corner"
    (let [cell             [0 0]
          board            [[{} {} {}]
                            [{} {} {}]
                            [{} {} {}]]
          exp-neighbours   [      [0 1]
                            [1 0] [1 1]]]
      (is (= exp-neighbours (neighbour-cells board cell))))))











