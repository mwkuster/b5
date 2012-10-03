(ns s2.test.squares
  (:use clojure.test
        s2.squares))

(deftest even-squares-loop-test
  (let 
      [sqs (even-squares-loop '(1 2 3 4 5 6))]
    (is (= sqs '(4 16 36)))))


