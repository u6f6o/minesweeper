(ns minesweeper.icons
  "Icons control utility"
  (:require
    [clojure.java.io :as io]
    [clojure.string  :as str]
    [seesaw.icon :as icon])
  (:import
    [java.io File]))


(def ^:private cell-icons-path "minesweeper/icons/cell")
(def ^:private face-icons-path "minesweeper/icons/face")


(defn- file-name
  [file]
  (str/replace-first
   (.getName file) #"\.[^.]+$" ""))


(def ^:private init-icons
  (memoize
   (fn [res]
     (let [parent (rest (file-seq (io/file (io/resource res))))]
       (reduce
        #(assoc %1 (keyword (file-name %2)) (icon/icon %2))
        {}
        parent)))))


(defn cell-icons
  [id]
  (let [icons (init-icons cell-icons-path)]
    (get icons id)))


(defn face-icons
  [id]
  (let [icons (init-icons face-icons-path)]
    (get icons id)))
