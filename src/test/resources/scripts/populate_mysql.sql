INSERT INTO apikeys (apikey, owner) VALUES ('913a73b565c8e2c8ed94497580f619397709b8b6', 'test@jimmikristensen.dk');

INSERT INTO asciidoc (title, apikeys_id, doc) VALUES ('Introduction to AsciiDoc', 1, '= Introduction to AsciiDoc
Doc Writer <doc@example.com>

A preface about http://asciidoc.org[AsciiDoc].

== First Section

* item 1
* item 2

[source,ruby]
puts "Hello, World!"');