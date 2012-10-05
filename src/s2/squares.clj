(ns s2.squares)

(defn even-squares-loop [l]
  "Takes a list of numbers and returns a list with the squares of the even numbers"
  (loop [lst l res '()]  
    (let [l (last lst)]
      (if (empty? lst)
        res
        (recur (drop-last lst) (if (even? l) (cons (* l l) res) res))))))

(defn even-squares-for [l]
  (for [i l :when (even? i)] (* i i)))

(defn even-squares-map [l]
  (map  #(* % %) (filter even? l)))