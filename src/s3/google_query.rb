require 'addressable/template'

template = Addressable::Template.new("https://www.google.de/#q={query}")

query_uris = ["abc", "def", "abcd", "abc def", "ruby", "ruby on rails"].collect {|u|
  template.expand("query" => u)
}

puts query_uris
