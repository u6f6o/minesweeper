(ns minesweeper.ui
  (:gen-class)
  (:require [clojure.java.io :as io]
            [minesweeper.dispatch :as disp]
            [minesweeper.game :as game])
  (:use [seesaw core mig]
        [minesweeper board icons]))


(def started (atom false))
(def game (atom (vector nil)))
(def ui (frame :title "Minesweeper" :on-close :exit))
(def mine-count (atom nil))


(def levels { :beginner     { :rows 8,  :cols 8,  :mines 10 }
              :intermediate { :rows 16, :cols 16, :mines 40 }
              :expert       { :rows 30, :cols 16, :mines 99 }})



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


(defn game-won
  [data]
  (do
    (uncover-field data)
    (config! (select ui [:#status]) :icon (face-icons :victory))
    (repaint! ui)))


(defn game-lost
  [data]
  (let [board (:board data)
        w     (count board)
        h     (count (first board))]
    (do
      (doseq [row (range w) col (range h)]
        (uncover-field row col (get-in board [row col])))
      (config! (select ui [:#status]) :icon (face-icons :defeat))
      (repaint! ui))))


(defn examine-field
  [data]
  (let [row        (:row data)
        col        (:col data)
        mine-count (:mines (:beginner levels))]
    (disp/fire :explore-field
               {:row row :col col :mine-count mine-count})))


(defn make-button [row col bg]
    (button :id     (str "field_" row "_" col)
            :icon   (cell-icons :button)
            :listen [:action(fn [e] (disp/fire :explore-field {:row row :col col :mine-count (:mines (:beginner levels))}))]
            :group  bg))


(defn make-board-panel
  [rows cols]
  (let [bg (button-group)]
    (mig-panel
     :constraints [(str "gap 0, wrap" rows) "[]" "[]" ]
     :items       (for [row (range rows) col (range cols)]
                    (vector (make-button row col bg) "w 24px!, h 24px!")))))


(defn make-info-panel
  []
  (label :id "status"
         :icon (face-icons :happy)))


(defn make-layout
  [rows cols]
  (mig-panel
   :constraints ["wrap1" "[center]" "[][]" ]
   :items       [[(make-info-panel)]
                 [(make-board-panel rows cols)]]))


(defn new-game
  ([])
  ([level])
  ([rows cols mines]))


(defn make-menubar
  []
  (vector (menu :text "File"
                :items [(menu-item :text "Beginner"
                                   :listen [:action (fn [e] (new-game (:beginner levels)))])
                        (menu-item :text "Intermediate"
                                   :listen [:action (fn [e] (new-game (:intermediate levels)))])
                        (menu-item :text "Expert"
                                   :listen [:action (fn [e] (new-game (:expert levels)))])])))


(defn make-ui
  [rows cols]
  (do
    (config! ui :content (make-layout rows cols))
    (config! ui :menubar (menubar :items (make-menubar)))
    (pack! ui)
    (config! ui :resizable? false)
    (show! ui)))


(defn new-game
  ([]
   (new-game (:beginner levels)))
  ([level]
   (new-game (:rows level)
             (:cols level)
             (:mines level)))
  ([rows cols mines]
   (do
     (reset! started false)
     (reset! game (empty-board rows cols))
     (reset! mine-count mines)
     (make-ui rows cols))))


(disp/register :game-won #'game-won)
(disp/register :game-lost #'game-lost)
(disp/register :uncover-field #'uncover-field)



(defn -main
  []
  (do
    (native!)
    (new-game)))
