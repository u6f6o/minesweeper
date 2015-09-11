(ns minesweeper.dispatch)


(def receivers (atom {}))


(defn register
  [evt f]
  (if
    (var? f)
    (swap! receivers
           #(assoc %1 %2 (conj (or (get %1 %2) #{}) %3))
           evt
           f)
    (throw
     (IllegalStateException. "wrapperd function must be a var"))))


