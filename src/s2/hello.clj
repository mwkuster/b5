(ns s2.hello)

(defn hello
  ([]
     (hello "Hello" "World"))
  ([name]
     (hello "Hello" name))
  ([phrase name]
     (str phrase ", " name)))