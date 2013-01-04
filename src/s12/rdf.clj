(ns s12.rdf
  (:import [com.hp.hpl.jena.rdf.model Model])
  (:use [seabass.core] :reload)
  (:use [clojure.java.io]))

(def model 
  (build ["src/s12/62007CJ0535.rdf" "RDF/XML"]
         ["src/s12/61960CC0002.ttl" "TTL"]))


(defn get-titles-by-celex [celex]
  (let
      [query (str
              "PREFIX cdm: <http://publications.europa.eu/ontology/cdm#> 
select ?expr ?title ?lang
{?work cdm:resource_legal_id_celex \"" celex "\"^^xsd:string .
 ?expr cdm:expression_belongs_to_work ?work .
 ?expr cdm:expression_title ?title .
 ?expr cdm:expression_uses_language ?lang .
 }")]
(print query)
(bounce query model)))