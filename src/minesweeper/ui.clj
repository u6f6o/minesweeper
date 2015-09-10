(ns minesweeper.ui
  (:gen-class)
  (:require [clojure.java.io :as io])
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


(defn expose-field
  [row col]
  (let [field (select-field row col)
        field-attrs (get-in @game [row col])]
    (config! field :icon (choose-icon field-attrs))))


(defn game-won
  []
  (do
    (config! (select ui [:#status]) :icon (face-icons :victory))
    (repaint! ui)))


(defn game-lost
  []
  (let [coords (to-coords @game)]
    (do
      (doseq [pos coords]
        (expose-field (first pos) (second pos)))
      (config! (select ui [:#status]) :icon (face-icons :defeat))
      (repaint! ui))))


(defn examine-field
  [row col]
  (do
    (when (not @started)
      (reset! game
              (place-warnings
               (place-mines @game @mine-count [row col])))
      (reset! started true))
    (swap! game #(explore-field % [row col]))
    (cond
     (game-won? @game) (game-won)
     (game-lost? @game) (game-lost)
     :else (expose-field row col))))


(defn make-button [row col bg]
    (button :id     (str "field_" row "_" col)
            :icon   (cell-icons :button)
            :listen [:action(fn [e] (examine-field row col))]
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


(defn -main
  []
  (do
    (native!)
    (new-game)))
