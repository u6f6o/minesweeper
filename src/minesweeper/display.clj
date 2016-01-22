(ns minesweeper.display)


(defn- state-transformer*
  [states formatter]
  (fn [cell-meta]
    (let [state-values (vals (select-keys cell-meta states))]
      (when (pos? (apply * state-values))
        (formatter state-values)))))


(def ^:private cell->mine
  (state-transformer* [:mine :expl] (constantly "[M]")))


(def ^:private cell->flag
  (state-transformer* [:flag] (constantly "[F]")))


(def ^:private cell->explored
  (state-transformer* [:expl] (constantly "[E]")))


(def ^:private cell->warning
  (state-transformer* [:warn :expl] #(str "[" (first %) "]")))


(def ^:private cell->void (constantly "[ ]"))


(defn cell->icon
  [cell-meta]
  (or
   (cell->mine cell-meta)
   (cell->warning cell-meta)
   (cell->explored cell-meta)
   (cell->flag cell-meta)
   (cell->void cell-meta)))


(defn board->icons
  [board]
  (partition (:width board)
    (map (comp cell->icon
               (partial zipmap [:expl :mine :warn :flag])
               vector)
         (:explored board)
         (:mines board)
         (:warnings board)
         (:flags board))))


(defn- format-board
  [board]
  (->> (board->icons board)
       (map (partial str/join " "))
       (str/join "\n")))


(def display-board
  (comp println format-board))
