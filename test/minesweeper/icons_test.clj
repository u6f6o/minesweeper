(ns minesweeper.icons-test
  (:require [clojure.test :refer :all]
            [minesweeper.icons :refer :all])
  (:import [javax.swing ImageIcon]))


(deftest test-cell-icons
  (testing "All cell icons present"
    (are [icon-key] (instance? ImageIcon (cell-icons icon-key))
         :0
         :1
         :2
         :3
         :4
         :5
         :6
         :7
         :8
         :button
         :flag
         :mine
         :redmine)))


(deftest test-face-icons
  (testing "All face icons present"
    (are [icon-key] (instance? ImageIcon (face-icons icon-key))
         :defeat
         :happy
         :oh
         :victory)))
