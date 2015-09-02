(ns minesweeper.game-test
  (:require [clojure.test :refer :all]
            [minesweeper.game :refer :all]))


(deftest test-win-conditions
  (testing "All fields cleared"
    (let [board [[{:explored true} {:explored true} {:explored true}]
                 [{:explored true} {:mine true}     {:explored true}]
                 [{:explored true} {:explored true} {:explored true}]]]
      (is
       (game-won? board))))
  (testing "Single covered field left"
    (let [board [[{:explored true} {:explored true} {:explored true}]
                 [{:explored true} {:mine true}     {:explored true}]
                 [{:explored true} {:explored true} {}]]]
      (is
       (not (game-won? board)))))
  (testing "No field uncovered"
    (let [board [[{} {}           {}]
                 [{} {:mine true} {}]
                 [{} {}           {}]]]
      (is
       (not (game-won? board)))))
  (testing "Empty board"
    (let [board [[{} {} {}]
                 [{} {} {}]
                 [{} {} {}]]]
      (is
       (not (game-won? board))))))


(deftest test-lose-conditions
  (testing "Uncover mined field"
    (let [board [[{} {}                           {}]
                 [{} {:mine true, :explored true} {}]
                 [{} {}                           {}]]]
      (is
       (game-lost? board))))
  (testing "All fields cleared"
    (let [board [[{:explored true} {:explored true} {:explored true}]
                 [{:explored true} {:mine true}     {:explored true}]
                 [{:explored true} {:explored true} {:explored true}]]]
      (is
       (not (game-lost? board)))))
  (testing "No field uncovered"
    (let [board [[{} {}           {}]
                 [{} {:mine true} {}]
                 [{} {}           {}]]]
      (is
       (not (game-lost? board)))))
  (testing "Empty board"
    (let [board [[{} {} {}]
                 [{} {} {}]
                 [{} {} {}]]]
      (is
       (not (game-lost? board))))))


(deftest test-neighbour-cells
  (testing "Get neighbour cells from center"
    (let [cell         [1 1]
          board        [[{} {} {}]
                        [{} {} {}]
                        [{} {} {}]]
          neighbours   [[0 0] [0 1] [0 2]
                        [1 0]       [1 2]
                        [2 0] [2 1] [2 2]]]
      (is
       (= neighbours (neighbour-cells board cell)))))
  (testing "Get neighbour cells from border"
    (let [cell         [1 0]
          board        [[{} {} {}]
                        [{} {} {}]
                        [{} {} {}]]
          neighbours   [[0 0] [0 1]
                        [1 1]
                        [2 0] [2 1]]]
      (is
       (= neighbours (neighbour-cells board cell)))))
  (testing "Get neighbour cells from corner"
    (let [cell         [0 0]
          board        [[{} {} {}]
                        [{} {} {}]
                        [{} {} {}]]
          neighbours   [      [0 1]
                        [1 0] [1 1]]]
      (is
       (= neighbours (neighbour-cells board cell))))))


(deftest test-place-warnings
  (testing "Multiple mines"
    (let [board [[{}           {}           {}           {}           {}          ]
                 [{:mine true} {:mine true} {}           {}           {}          ]
                 [{}           {}           {}           {}           {}          ]
                 [{}           {}           {:mine true} {:mine true} {:mine true}]
                 [{}           {}           {:mine true} {}           {:mine true}]
                 [{:mine true} {}           {:mine true} {:mine true} {:mine true}]]

          exp   [[{:warn 2   } {:warn 2   } {:warn 1   } {}           {}          ]
                 [{:mine true} {:mine true} {:warn 1   } {}           {}          ]
                 [{:warn 2   } {:warn 3   } {:warn 3   } {:warn 3   } {:warn 2   }]
                 [{}           {:warn 2   } {:mine true} {:mine true} {:mine true}]
                 [{:warn 1   } {:warn 4   } {:mine true} {:warn 8   } {:mine true}]
                 [{:mine true} {:warn 3   } {:mine true} {:mine true} {:mine true}]]]
      (is
       (= exp (place-warnings board)))))
  (testing "Single mine"
    (let [board [[{          } {          } {          }]
                 [{          } {:mine true} {          }]
                 [{          } {          } {          }]]

          exp   [[{:warn 1   } {:warn 1   } {:warn 1   }]
                 [{:warn 1   } {:mine true} {:warn 1   }]
                 [{:warn 1   } {:warn 1   } {:warn 1   }]]]
      (is
       (= exp (place-warnings board)))))
  (testing "Empty board"
    (let [board [[{          } {          } {          }]
                 [{          } {          } {          }]
                 [{          } {          } {          }]]

          exp   [[{          } {          } {          }]
                 [{          } {          } {          }]
                 [{          } {          } {          }]]]
      (is
       (= exp (place-warnings board))))))



(deftest test-warnings-freq
  (testing "Multiple mines"
    (let [board [[{}           {}           {}           {}           {}          ]
                 [{:mine true} {:mine true} {}           {}           {}          ]
                 [{}           {}           {}           {}           {}          ]
                 [{}           {}           {:mine true} {:mine true} {:mine true}]
                 [{}           {}           {:mine true} {}           {:mine true}]
                 [{:mine true} {}           {:mine true} {:mine true} {:mine true}]]
          exp   {[0 0] 2
                 [0 1] 2
                 [0 2] 1
                 [1 2] 1
                 [2 0] 2
                 [2 1] 3
                 [2 2] 3
                 [2 3] 3
                 [2 4] 2
                 [3 1] 2
                 [4 0] 1
                 [4 1] 4
                 [4 3] 8
                 [5 1] 3}]
      (is
       (= exp (warnings-freq board))))))
