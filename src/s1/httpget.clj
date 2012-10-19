(ns s1.httpget
  (:import (java.net Socket)))

;Version that does not use an HTTP library to do an HTTP GET
;Instead uses low-level socket to read and write the HTTP request / response pair

(defn write-request [^java.io.OutputStream out host page]
  (let
      [req (str "GET " page " HTTP/1.1
Host: " host "

")]
    (print req)
    (.write out (.getBytes req))
    (println "Request written")))

(defn read-response [^java.io.InputStream in]
  "Very low-level implementation of an HTTP response"
  ;; loop on (.read in)
  (loop 
      [c 20 res-str ""]
    (if (> c 0)
      (recur (.read in) (str res-str (char c)))
      res-str)))

(defn get-page [^String host ^Integer port ^String page]
  (let
      [conn (Socket. host port)
       in (.getInputStream conn)
       out (.getOutputStream conn)]
    (write-request out host page)
    ;;http://clojuredocs.org/clojure_core/clojure.core/future
    (future (read-response in))))

;;(get-page "www.dradio.de" 80 "/")

