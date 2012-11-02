(ns s6.atmos
  (:import (java.io Writer)
           (java.nio.charset Charset)
           (org.apache.commons.codec.binary Hex Base64)
           (javax.crypto Mac)
           (javax.crypto.spec SecretKeySpec))
  (:require [clojure.string :as s]
            [clojure.data.codec.base64 :as base64]))
;            [net.djpowell.b64.core]))

(defn canonicalize-emc-headers [headers]
  "Bring the x-emc specific extension headers into a canonical form"
  (let
      [emc-headers (sort (filter #(re-find #"^x-emc" %) (keys headers)))]
    (s/join "\n" (map #(s/replace (str % ":" (get headers %)) #"\s+" " ") emc-headers))))

(defn hashstring [http-method path headers]
  "Calculate the hashstring for an http request following the rules set out by ATMOS"
  (s/join
    "\n"
    (list
     http-method (get headers "content-type") (get headers "range") (get headers "date")
     (s/lower-case path) (canonicalize-emc-headers headers))))
  ; #"\n([/\w]+)" "\n  $1"))

(defn sign [secret-key http-method path headers]
  "Sign a hashstring with secret-key. The method to be applied is defined to be signature = Base64(HMACSHA1(HashString))
In Python: 
decodedkey = base64.b64decode(secret)    
hash = hmac.new(decodedkey, headers, hashlib.sha1).digest()
hashout = base64.encodestring(hash).strip()                                            
return hashout"
  (let
      ;Partially inspired by https://github.com/djpowell/b64/blob/master/src/net/djpowell/b64/core.clj
                                        ;and http://stackoverflow.com/questions/3208160/how-to-generate-an-hmac-in-java-equivalent-to-a-python-example
      [ascii (Charset/forName "us-ascii")
       enc-bytes (.getBytes secret-key ascii)
       decoded-key-bytes (base64/decode enc-bytes)
       decoded-key-str (String. decoded-key-bytes ascii)
       keyspec (SecretKeySpec. decoded-key-bytes "HmacSHA1")
       mac  (Mac/getInstance "HmacSHA1")
       hashstring (hashstring http-method path headers)] 
    (.init mac keyspec)
    (s/trim
     (Base64/encodeBase64String 
      (.doFinal mac 
                (.getBytes hashstring))))))