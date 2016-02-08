(ns minesweeper.ui
  (:gen-class)
  (:import (java.awt.event MouseEvent))
  (:require [clojure.java.io :as io]
            [minesweeper.game :as game]
            [minesweeper.display :as display]
            [minesweeper.icons :as icons])
  (:use [seesaw core mig]))

(def ui (frame :title "Minesweeper" :on-close :exit))
(def board (atom {}))

(defn select-field
  [idx]
  (select ui [(keyword (str "#field_" idx))]))

(defn- update-fields
  [cell-states]
  (doseq [[idx state] (map-indexed vector cell-states)
          :let [field (select-field idx)]]
      (config! field :icon (icons/cell-icons state))))

(defn- change-smiley
  [face]
  (config! (select ui [:#status])
           :icon (icons/face-icons face)))

(defn- update-board
  [snapshot face]
  (do
    (change-smiley face)
    (update-fields (:cells snapshot))
    (repaint! ui)))

(defn- play
  [snapshot]
  (update-board snapshot :happy))

(defn- announce-victory
  [snapshot]
  (update-board snapshot :victory))

(defn- announce-defeat
  [snapshot]
  (update-board snapshot :defeat))

(defn explore-field
  [idx]
  (let [swap-fn  (fn [board idx]
                   (if-not (game/started? board)
                     (game/init board idx)
                     (game/explore board idx)))
        snapshot (display/snapshot
                   (swap! board swap-fn idx))]
    (condp = (:state snapshot)
      :won  (announce-victory snapshot)
      :lost (announce-defeat snapshot)
      (play snapshot))))

(defn toggle-flag
  [idx]
  (let [swap-fn  (fn [board idx]
                   (game/flag board idx))
        snapshot (display/snapshot
                   (swap! board swap-fn idx))]
    (play snapshot)))

(defn make-ui [snapshot])

(defn new-game
  [w h m]
  (reset! board (game/empty-board w h m))
  (make-ui (display/snapshot @board)))

(defn- make-button
  [idx bg]
  (letfn [(left-click? [e] (= (.getButton e) MouseEvent/BUTTON1))]
    (button :id     (str "field_" idx)
            :icon   (icons/cell-icons :hidden)
            :listen [:mouse-clicked
                     (fn [e]
                       (if (left-click? e)
                         (explore-field idx)
                         (toggle-flag idx)))]
            :group  bg)))

(defn- make-board-panel
  [snapshot]
  (let [bg    (button-group)
        [n m] (:dimension snapshot)]
    (mig-panel
     :constraints [(str "gap 0, wrap" n) "[]" "[]" ]
     :items       (for [idx (range (* n m))]
                    (vector (make-button idx bg) "w 24px!, h 24px!")))))

(defn make-info-panel
  []
  (label :id "status"
         :icon (icons/face-icons :happy)))

(defn make-layout
  [snapshot]
  (mig-panel
   :constraints ["wrap1" "[center]" "[][]" ]
   :items       [[(make-info-panel)]
                 [(make-board-panel snapshot)]]))

(defn make-menubar
  []
  (vector (menu :text "File"
                :items [(menu-item :text "New game"
                                   :listen [:action (fn [e] (new-game 8 8 10))])])))

(defn make-ui
  [snapshot]
  (config! ui :content (make-layout snapshot))
  (config! ui :menubar (menubar :items (make-menubar)))
  (pack! ui)
  (config! ui :resizable? false)
  (show! ui))

(defn -main []
   (do
    (native!)
    (new-game 8 8 10)))

(-main)
