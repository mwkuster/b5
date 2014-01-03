require 'rdf'
require 'sparql'
require 'rdf/turtle'
require 'rdf/rdfxml'
#require 'rdf/trig'

root = "http://publications.europa.eu/resource/celex/"

#Repository
repo = RDF::Repository.new(:with_context => true)

#Using named graphs in the repository
repo.load("62007CJ0535.rdf", :context => RDF::URI(root + "62007CJ0535"), :format => :rdfxml)
repo.load("61960CC0002.ttl", :context => RDF::URI(root + "61960CC0002"), :format => :ttl)
repo.load("http://publications.europa.eu/resource/celex/62012CJ0001", 
          options={:headers => {"Accept" => "application/rdf+xml;notice=tree"}, :context => RDF::URI(root + "62012CJ0001"), :format => :rdfxml})

def output_all_triples (repository)
   query = <<-sparql
PREFIX cdm: <http://publications.europa.eu/ontology/cdm#> 
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
SELECT ?s ?p ?o ?gra
WHERE {
 GRAPH ?gra {
 ?s ?p ?o
}
}
sparql
  puts query
              
  solutions = SPARQL.execute(query, repository)
  puts "All quads:"
  solutions.each { |sol| puts sol.to_hash }
  nil
end

def find_celexes (repo)
   query = <<-sparql
PREFIX cdm: <http://publications.europa.eu/ontology/cdm#> 
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
SELECT ?gra ?work ?celex
WHERE
{
GRAPH ?gra {
 ?work cdm:resource_legal_id_celex  ?celex .
}
}
sparql
  puts query
              
  solutions = SPARQL.execute(query, repo)
  solutions.each { |sol| puts sol.to_hash }  
end

output_all_triples(repo)

find_celexes(repo)
