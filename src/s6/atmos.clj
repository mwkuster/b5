(ns s6.atmos
  (:import (java.io Writer)
           (java.nio.charset Charset)
           (org.apache.commons.codec.binary Hex Base64)
           (javax.crypto Mac)
           (javax.crypto.spec SecretKeySpec))
  (:require [clojure.string :as s]
            [clojure.data.codec.base64 :as base64]
            [clj-http.client :as client]
            [clj-time.core :as time]
            [clj-time.local :as local]
            [clj-time.format :as format]))

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

(defn get-date-time []
  "Get the current datetime in RFC822 format (the one required by http)"
  ;:rfc822
  (local/format-local-time (time/now) :rfc822))

(defn create-object [uuid secret-key user-id content listable-metadata]
  "Create an object in EMC2"
  (let
      [headers {
                "content-type"  "application/octet-stream",
                "x-emc-uid"  uuid,
                "x-emc-listable-meta" listable-metadata,
                "x-emc-date"  (get-date-time),
                "x-emc-useracl" user-id,
                }
       signature (sign secret-key "POST" "/rest/objects" headers)
       headers-with-signature (assoc headers "x-emc-signature" signature)]
    (println headers-with-signature)
    (client/post "https://api.atmosonline.com/rest/objects"
                 {:body content 
                  :accept :json
                  :headers headers-with-signature}))) 

(defn create-object-in-namespace [uuid secret-key user-id dir content listable-metadata]
  "Create an object in EMC2 in a logical namespace"
  (let
      [headers {
                "content-type"  "application/octet-stream",
                "x-emc-uid"  uuid,
                "x-emc-listable-meta" listable-metadata,
                "x-emc-date"  (get-date-time),
                "x-emc-useracl" user-id,
                }
       path (str "/rest/namespace" dir)
       signature (sign secret-key "POST" path  headers)
       headers-with-signature (assoc headers "x-emc-signature" signature)]
    (println headers-with-signature)
    (client/post (str "https://api.atmosonline.com/" path)
                 {:body content 
                  :accept :json
                  :headers headers-with-signature}))) 

(defn get-content [uuid secret-key object-id]
  "Get back the content for an object"
  (let
      [headers {
                "content-type"  "application/octet-stream",
                "x-emc-uid"  uuid,
                "x-emc-date"  (get-date-time),
                }]
    (client/get (str "https://api.atmosonline.com/rest/objects/" object-id) 
                {:headers (assoc headers "x-emc-signature"
                                 (sign secret-key "GET" (str "/rest/objects/" object-id) headers))})))

(defn get-system-metadata [uuid secret-key object-id]
  "Get system-level metadata for an object"
  (let
      [headers {
                "content-type"  "application/octet-stream",
                "x-emc-uid"  uuid,
                "x-emc-date"  (get-date-time),
                }
       path (str "/rest/objects/" object-id "?metadata/system")]
    (client/get (str "https://api.atmosonline.com" path)
                {:headers (assoc headers "x-emc-signature"
                                 (sign secret-key "GET" path headers))})))

(defn get-user-metadata [uuid secret-key object-id]
  "Get system-level metadata for an object"
  (let
      [headers {
                "content-type"  "application/octet-stream",
                "x-emc-uid"  uuid,
                "x-emc-date"  (get-date-time),
                }
       path (str "/rest/objects/" object-id "?metadata/user")]
    (client/get (str "https://api.atmosonline.com" path)
                {:headers (assoc headers "x-emc-signature"
                                 (sign secret-key "GET" path headers))})))