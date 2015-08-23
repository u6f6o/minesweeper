(ns minesweeper.util
  "File control utility"
  (:require
    [clojure.java.io :as io]
    [clojure.string  :as str])
  (:import [java.io File]))


(defmulti basename
  class)
(defmethod basename File
  [file]
  (basename (.getName file)))
(defmethod basename String
  [s]
  (str/replace-first s #"\.[^.]+$" ""))


(defn list-dir
  [resource]
  (let [fs (->> "minesweeper/icons/"
                io/resource
                io/file
                file-seq)]
