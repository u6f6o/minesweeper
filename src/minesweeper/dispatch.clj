(ns minesweeper.dispatch)


(def ^:private receivers (atom {}))


(defn register
  [evt trigger]
  (if
    (var? trigger)
    (swap! receivers
           #(assoc %1 %2 (conj (or (get %1 %2) #{}) %3))
           evt
           trigger)
    (throw
     (IllegalStateException. "trigger must be a function wrapped in a var"))))


(defn fire
  [evt data]
  (doseq [trigger (get @receivers evt)]
    (eval (trigger data))))


