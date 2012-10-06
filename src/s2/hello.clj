(ns s2.hello)

(defn hello
  ([]
     (hello "World"))
  ([name]
     (hello "Hello" name))
  ([phrase name]
     (str phrase ", " name)))