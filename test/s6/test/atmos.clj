(ns s6.test.atmos
  (:use clojure.test
        s6.atmos))

;Example taken from Atmos Programmers Guide 2.01, p. 99ff
(deftest hashstring-test
  (let
      [headers {"x-emc-date" "Thu, 05 Jun 2008 16:38:19 GMT",
                "x-emc-groupacl" "other=NONE",
                "host" "10.5.115.118",
                "content-length" "4286",
                "x-emc-uid" "6039ac182f194e15b9261d73ce044939/user1",
                "x-emc-listable-meta" "part4/part7/part8=quick",
                "x-emc-meta" "part1=buy",
                "accept" "*/*",
                "x-emc-useracl" "john=FULL_CONTROL,mary=WRITE",
                "date" "Thu, 05 Jun 2008 16:38:19 GMT",
                "content-type" "application/octet-stream"}
       method "POST"
       path "/rest/objects"
       secret-key "LJLuryj6zs8ste6Y3jTGQp71xq0="]
  (is
   (= (canonicalize-emc-headers headers) "x-emc-date:Thu, 05 Jun 2008 16:38:19 GMT
x-emc-groupacl:other=NONE
x-emc-listable-meta:part4/part7/part8=quick
x-emc-meta:part1=buy
x-emc-uid:6039ac182f194e15b9261d73ce044939/user1
x-emc-useracl:john=FULL_CONTROL,mary=WRITE"))
  (is
   (= (hashstring method path headers) "POST\napplication/octet-stream\n\nThu, 05 Jun 2008 16:38:19 GMT\n/rest/objects\nx-emc-date:Thu, 05 Jun 2008 16:38:19 GMT\nx-emc-groupacl:other=NONE\nx-emc-listable-meta:part4/part7/part8=quick\nx-emc-meta:part1=buy\nx-emc-uid:6039ac182f194e15b9261d73ce044939/user1\nx-emc-useracl:john=FULL_CONTROL,mary=WRITE"))
  (is
   (= (sign secret-key method path headers) "WHJo1MFevMnK4jCthJ974L3YHoo="))
   ))
