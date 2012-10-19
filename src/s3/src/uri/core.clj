(ns uri.core)

(use 'uritemplate-clj.core)

(defn -main []
  "Print result of Google uri template"
  (println  (uritemplate "https://www.google.de/#hl={language}&q={query}" 
{"language" "de", "query" "politik in worms"})))
