class CreateLoggedRequests < ActiveRecord::Migration
  def self.up
    create_table :logged_requests do |t|
      t.string  :date
      t.string  :ps
      t.string  :psid
      t.string  :accept
      t.string  :accept_language
      t.string  :not_modified
    end
  end

 def self.down
    drop_table :logged_requests
  end
end
