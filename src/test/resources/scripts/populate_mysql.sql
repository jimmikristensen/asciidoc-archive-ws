INSERT INTO apikeys (apikey, owner) VALUES ('913a73b565c8e2c8ed94497580f619397709b8b6', 'test@jimmikristensen.dk');

INSERT INTO asciidoc (title, apikeys_id, creationDate, doc) VALUES ('Introduction to AsciiDoc', 1, '2015-03-31 20:59:59', '= Introduction to AsciiDoc
Doc Writer <doc@example.com>

A preface about http://asciidoc.org[AsciiDoc].

== First Section

* item 1
* item 2

[source,ruby]
puts "Hello, World!"');

INSERT INTO asciidoc (title, apikeys_id, creationDate, doc) VALUES ('Example of AsciiDoc', 1, '2015-03-30 20:25:01', '= Example of AsciiDoc
Doc Writer <doc@example.com>

A preface about http://asciidoc.org[AsciiDoc].

== First Section

* item 1
* item 2

[source,java]
puts "Hello, World!"');

INSERT INTO translation (`type`, asciidoc_id, doc) VALUES ('html5', 1, '<div id="preamble">
    <div class="sectionbody">
    <div class="paragraph">
    <p>A preface about <a href="http://asciidoc.org">AsciiDoc</a>.</p>
    </div>
    </div>
    </div>
    <div class="sect1">
    <h2 id="_first_section">First Section</h2>
    <div class="sectionbody">
    <div class="ulist">
    <ul>
    <li>
    <p>item 1</p>
    </li>
    <li>
    <p>item 2</p>
    </li>
    </ul>
    </div>
    <div class="listingblock">
    <div class="content">
    <pre class="highlight"><code class="language-ruby" data-lang="ruby">puts "Hello, World!"</code></pre>
    </div>
    </div>
    </div>
    </div>');
    
INSERT INTO category (name, asciidoc_id) VALUES ('Introduction', 1);
INSERT INTO category (name, asciidoc_id) VALUES ('Asciidoc', 1);
INSERT INTO category (name, asciidoc_id) VALUES ('Asciidoc', 2);