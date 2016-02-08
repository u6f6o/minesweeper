(ns minesweeper.display
  (:require [clojure.string :as str]
            [minesweeper.game :as game]))

(defn- state-transformer*
  [states formatter]
  (fn [cell-meta]
    (let [state-values (vals (select-keys cell-meta states))]
      (when (pos? (apply * state-values))
        (formatter state-values)))))

(def ^:private cell->flagmine
  (state-transformer* [:mine :flag] (constantly :flagmine)))

(def ^:private cell->redmine
  (state-transformer* [:mine :expl] (constantly :redmine)))

(def ^:private cell->mine
  (state-transformer* [:mine] (constantly :mine)))

(def ^:private cell->flag
  (state-transformer* [:flag] (constantly :flag)))

(def ^:private cell->explored
  (state-transformer* [:expl] (constantly :0)))

(def ^:private cell->warning
  (state-transformer* [:warn] #(keyword (str (first %)))))

(def ^:private cell->explored-warning
  (state-transformer* [:warn :expl] #(keyword (str (first %)))))

(defn- cell->icon
  [cell-meta game-ended?]
  (if game-ended?
    (or
     (cell->flagmine cell-meta)
     (cell->redmine cell-meta)
     (cell->mine cell-meta)
     (cell->warning cell-meta)
     (cell->flag cell-meta)
     :0)
    (or
     (cell->flag cell-meta)
     (cell->explored-warning cell-meta)
     (cell->explored cell-meta)
     :hidden)))

(defn- board->meta
  [board]
  (map (comp (partial zipmap [:expl :mine :warn :flag]) vector)
       (:explored board)
       (:mines board)
       (:warnings board)
       (:flags board)))

(defn- meta->states
  [board-meta game-ended?]
  (map #(cell->icon % game-ended?) board-meta))

(defn- cells->states
  [board]
  (let [game-ended? (or (game/won? board) (game/lost? board))]
    (-> board
        (board->meta)
        (meta->states game-ended?))))

(defn- game-state
  [board]
  (cond
    (game/won? board) :won
    (game/lost? board) :lost
    :else :started))

(defn snapshot
  [board]
  { :cells     (cells->states board),
    :dimension (game/dimension board),
    :mines     (game/mine-count board),
    :state     (game-state board)})

(defn- format-cell
  [cell]
  (case cell
    :0        "[E]"
    :1        "[1]"
    :2        "[2]"
    :3        "[3]"
    :4        "[4]"
    :5        "[5]"
    :6        "[6]"
    :7        "[7]"
    :8        "[8]"
    :flag     "[F]"
    :flagmine "[M]"
    :redmine  "[M]"
    :mine     "[M]"
              "[ ]"))

(defn- format-board
  [board]
  (->> (cells->states board)
       (map format-cell)
       (partition (:width board))
       (map (partial str/join " "))
       (str/join "\n")))

(defn- format-display
  [board]
  (let [board-text (format-board board)]
    (cond (game/won? board)  (str board-text "\n\nYOU WIN! :-)")
          (game/lost? board) (str board-text "\n\nYOU LOSE! :-(")
          :else board-text)))

(def display-board
  (comp println format-display))
