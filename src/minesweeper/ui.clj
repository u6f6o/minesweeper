(ns minesweeper.ui
  (:gen-class)
  (:import (java.awt.event MouseEvent))
  (:require [clojure.java.io :as io]
            [minesweeper.dispatch :as disp]
            [minesweeper.game :as game])
  (:use [seesaw core mig]
        [minesweeper icons]))


(def ui (frame :title "Minesweeper" :on-close :exit))


(defn select-field
  [row col]
  (select ui [(keyword (str "#field_" row "_" col))]))


(defn choose-icon
  [field-attrs]
  (letfn [(redmine? [m] (every? m [:explored :mine]))
          (mine?    [m] (:mine m))
          (warn?    [m] (:warn m))
          (flag?    [m] (:flag m))]
    (cond
     (redmine? field-attrs) (cell-icons :redmine)
     (flag? field-attrs)    (cell-icons :flag)
     (mine? field-attrs)    (cell-icons :mine)
     (warn? field-attrs)    (cell-icons (keyword (str (:warn field-attrs))))
     :else                  (cell-icons :0))))


(defn uncover-field
  ([data]
   (let [row   (:row data)
         col   (:col data)
         attrs (:attrs data)]
     (uncover-field row col attrs)))
  ([row col attrs]
   (let [field (select-field row col)]
     (config! field :icon (choose-icon attrs)))))


(defn announce-victory
  [data]
  (do
    (uncover-field data)
    (config! (select ui [:#status]) :icon (face-icons :victory))
    (repaint! ui)))


(defn announce-defeat
  [data]
  (let [board (:board data)
        w     (count board)
        h     (count (first board))]
    (do
      (doseq [row (range w) col (range h)]
        (uncover-field row col (get-in board [row col])))
      (config! (select ui [:#status]) :icon (face-icons :defeat))
      (repaint! ui))))


(defn new-game
  [level]
  (disp/fire :new-game {:level level}))


(defn make-button
  [row col level bg]
  (letfn [(left-click? [e] (= (.getButton e) MouseEvent/BUTTON1))]
    (button :id     (str "field_" row "_" col)
            :icon   (cell-icons :button)
            :listen [:mouse-clicked
                     (fn [e]
                       (if (left-click? e)
                         (disp/fire :explore-field {:row row :col col :level level})
                         (disp/fire :toggle-flag {:row row :col col})))]
            :group  bg)))


(defn make-board-panel
  [rows cols level]
  (let [bg (button-group)]
    (mig-panel
     :constraints [(str "gap 0, wrap" rows) "[]" "[]" ]
     :items       (for [row (range rows) col (range cols)]
                    (vector (make-button row col level bg) "w 24px!, h 24px!")))))


(defn make-info-panel
  []
  (label :id "status"
         :icon (face-icons :happy)))


(defn make-layout
  [rows cols level]
  (mig-panel
   :constraints ["wrap1" "[center]" "[][]" ]
   :items       [[(make-info-panel)]
                 [(make-board-panel rows cols level)]]))


(defn make-menubar
  []
  (vector (menu :text "File"
                :items [(menu-item :text "Beginner"
                                   :listen [:action (fn [e] (new-game (:beginner game/levels)))])
                        (menu-item :text "Intermediate"
                                   :listen [:action (fn [e] (new-game (:intermediate game/levels)))])
                        (menu-item :text "Expert"
                                   :listen [:action (fn [e] (new-game (:expert game/levels)))])])))


(defn make-ui
  [data]
  (let [level (:level data)
        rows  (:rows level)
        cols  (:cols level)]
    (do
      (config! ui :content (make-layout rows cols level))
      (config! ui :menubar (menubar :items (make-menubar)))
      (pack! ui)
      (config! ui :resizable? false)
      (show! ui))))


(disp/register :game-won #'announce-victory)
(disp/register :game-lost #'announce-defeat)
(disp/register :game-initialized #'make-ui)
(disp/register :uncover-field #'uncover-field)


(defn -main
  []
  (do
    (native!)
    (new-game (:beginner game/levels))))
