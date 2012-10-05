(ns httpget
  (:import (java.net Socket)
           (java.io PrintWriter InputStreamReader BufferedReader)))

;Version that does not use an HTTP library to do an HTTP GET
;Instead uses low-level socket to read and write the HTTP request / response pair
;Normally you'd work with threads or futures to get avoid blocking the REPL
(def host "www.google.de")
(def port 80)

(defn write-request [out host]
  (let
      [req (str "GET / HTTP/1.1
Host: " host "

")]
    (print req)
    (.write out (.getBytes req))
    (println "Request written")))

(defn read-response [in]
  "Very low-level implementation of an HTTP response"
  ;; loop on (.read in)
  (def c (atom 20)) ;set to an abritrary initial value (space in this case)
  (defn read-next [s] (.read in))
  (while (> @c 0)
    (do 
      (print (char @c))
      (swap! c read-next)))
  (println "Response received"))

(defn get-page [host port]
  (let
      [conn (Socket. host port)
       in (.getInputStream conn)
       out (.getOutputStream conn)]
    (.setReceiveBufferSize conn 1)
    (write-request out host)
    (read-response in)))



