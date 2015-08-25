(ns minesweeper.file
  "File control utility"
  (:require
    [clojure.java.io :as io]
    [clojure.string  :as str])
  (:import
    [java.io File]
    [java.net URL]))


(defmulti basename
  class)
(defmethod basename File
  [file]
  (basename (.getName file)))
(defmethod basename String
  [s]
  (str/replace-first s #"\.[^.]+$" ""))


(defn res-as-file
  [res]
  (io/file (io/resource res)))


(defmulti list-dir
  class)
(defmethod list-dir URL
  [resource]
  (list-dir (res-as-file resource)))
(defmethod list-dir File
  [file]
  (rest (file-seq file)))

