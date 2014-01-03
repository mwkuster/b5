require 'rdf'
require 'sparql'
require 'rdf/turtle'

repo = RDF::Repository.new
repo.load("61960CC0002.ttl", :format => :ttl)

require 'rdf/rdfxml'
repo.load("62007CJ0535.rdf", :format => :rdfxml)
repo.load("http://publications.europa.eu/resource/celex/62012CJ0001", 
          options={:headers => {"Accept" => "application/rdf+xml;notice=tree"}, :format => :rdfxml})

#Just for debugging: show the contents of the repository
#repo.each_statement { |statement| puts statement }


def output_all_triples (repository)
   query = <<-sparql
PREFIX cdm: <http://publications.europa.eu/ontology/cdm#> 
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
SELECT ?s ?p ?o
WHERE {
 ?s ?p ?o
}
sparql
  puts query
              
  solutions = SPARQL.execute(query, repository)
  puts "All triples:"
  solutions.each { |sol| puts sol.to_hash }
  nil
end

def titles_by_celex (repo, celex)
  "Returns all the titles and their languages for a given celex"
   query = <<-sparql
PREFIX cdm: <http://publications.europa.eu/ontology/cdm#> 
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
SELECT ?expr ?title ?lang
WHERE
{
 ?work cdm:resource_legal_id_celex  "#{celex}"^^xsd:string .
 ?expr cdm:expression_belongs_to_work ?work .
 ?expr cdm:expression_title ?title .
 ?expr cdm:expression_uses_language ?lang .
}
sparql
  puts query
              
  solutions = SPARQL.execute(query, repo)
  #puts "Titles:"
  #puts solutions.each { |sol| puts sol.to_hash}

  solutions.collect { |sol| sol[:title].to_s}
end

output_all_triples(repo)

titles = titles_by_celex(repo, "62012CJ0001")
puts titles
