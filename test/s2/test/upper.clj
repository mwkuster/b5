(ns s2.test.upper
  (:use clojure.test
        s2.upper))

(deftest upper-test
  (is 
   (= (upper-as '("xyz" "cde" "abc" "bab" "axx")) '("ABC" "AXX")))
  (is 
   (= (upper-as '("xyz" "axyz" "Axyz")) '("AXYZ")))
  (is 
   (= (upper-as '("aXYZ" "abc" "Axyz")) '("AXYZ" "ABC"))))
