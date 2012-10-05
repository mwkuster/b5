(ns s2.test.hello
  (:use clojure.test
        s2.hello))

(deftest hello-test
    (is (= (hello) "Hello, World"))
    (is (= (hello "B5") "Hello, B5"))
    (is (= (hello "Hallo" "B5") "Hallo, B5")))