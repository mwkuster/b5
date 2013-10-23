require 'csv'

class LoggedRequest2
  attr_reader :date, :ps, :psid, :accept, :accept_language, :not_modified
  def initialize(line)
    #Typische Zeile:
    #2013-08-01 00:00:05,121 [SOURCE] INFO  CMR_DISSEMINATION [eu.europa.ec.opoce.cellar.server.dissemination.ResourceRequestNegotiator]: New request for: resource [ps 'oj' - psid 'JOC_2013_095_R_TOC' - cellarid 'cellar:ef979b5c-9c3b-11e2-ab01-01aa75ed71a1'] - accept types [application/xhtml+xml, text/html;q=1.0] - accept languages [eng, fra;q=1.0, deu;q=0.99, bul;q=0.98, spa;q=0.97, ces;q=0.96, dan;q=0.95, est;q=0.94, ell;q=0.93, gle;q=0.92, hrv;q=0.91, ita;q=0.9, lav;q=0.89, isl;q=0.88, lit;q=0.87, hun;q=0.86, mlt;q=0.85, nld;q=0.84, nor;q=0.83, pol;q=0.82, por;q=0.81, ron;q=0.8, slk;q=0.79, slv;q=0.78, fin;q=0.77, swe;q=0.76] - IfNotModified 'null'
    begin
      match_data = /([0-9 :-]+),.*\[ps '([a-z]+)' - psid '([a-zA-Z0-9_%\.-]+)' .* - accept types \[([^\]]*)\] - accept languages \[([^\]]*)\] - IfNotModified '([a-zA-Z0-9-]+)'/.match(line)
      @date, @ps, @psid, @accept, @accept_language, @not_modified = match_data[1..-1]
    rescue
      raise "Could not parse '#{line}'"
    end
  end

  def to_csv()
    [@date, @ps, @psid, @accept, @accept_language, @not_modified].to_csv
  end

end

log = File.open(ARGV[0], "r")
requests = log.grep(/.*New request for: resource.*/).collect { |line|
  begin
    LoggedRequest2.new line
  rescue Exception => e
    puts e.message
    nil
  end
}.select {|req| !req.nil? }

puts requests.length

File.open(ARGV[1], "w") { |csv|
  requests.each { |req|
    csv << req.to_csv
  }
}
