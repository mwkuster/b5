(ns s2.upper)

(defn starts-with-a? [s] 
  (= \a (first s)))

(defn upper-as [lst]
  "Takes a list of strings and returns an upper-case version of those strings that start with a lowercase a"
  (map #(clojure.string/upper-case %) (filter starts-with-a? lst)))