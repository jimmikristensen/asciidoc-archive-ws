INSERT INTO apikeys (apikey, owner) VALUES ('913a73b565c8e2c8ed94497580f619397709b8b6', 'test@somewhere.org');

INSERT INTO asciidocs (title, filename, path, sha, created, url) 
VALUES (
'Asciidoc Test 1',
'asciidoc-testcase55.adoc', 
'test1/asciidoc-testcase55.adoc', 
'd40c3e54df31c876d426a507f6dec644cb895d4f', 
'2015-04-01 10:00:00', 
'');

INSERT INTO asciidocs (title, filename, path, sha, created, url) 
VALUES (
'Asciidoc Test 2',
'asciidoc-testcase16.adoc', 
'test1/asciidoc-testcase16.adoc', 
'5702e000b4419a4d5162655775d438fa14b4d747', 
'2015-04-02 10:00:00', 
'');

INSERT INTO asciidocs (title, filename, path, sha, created, url) 
VALUES (
'Asciidoc Test 3',
'asciidoc-testcase1.adoc', 
'test1/asciidoc-testcase17.adoc', 
'7acb36108793faf9ea201e1ad46e58280f75b5a1', 
'2015-04-03 10:00:00', 
'');

INSERT INTO asciidocs (title, filename, path, sha, created, url) 
VALUES (
'Asciidoc Test 1',
'asciidoc-testcase98.adoc', 
'test2/asciidoc-testcase98.adoc', 
'd30c3e54df31c876d426a507f6dec644cb895d4f', 
'2015-06-01 11:00:00', 
'');

INSERT INTO contents (asciidocId, `type`, doc) 
VALUES (
1, 
'ADOC', 
'= Another Introduction to AsciiDocc
Doc Writer <doc@example.dk>

A preface about http://asciidoc.org[AsciiDoc].

== First Section

* item 1
* item 2

[source,ruby]
puts "Hello, World!"'
);

INSERT INTO contents (asciidocId, `type`, doc) 
VALUES (
2, 
'ADOC', 
'= The Brothers Karamazov2
:author: Fyodor Dostoyevsky
:encoding: iso-8859-1
:plaintext:

..........................................................................
Translated from the Russian of Fyodor Dostoyevsky by Constance Garnett
The Lowell Press New York

 :
 :

***START OF THE PROJECT GUTENBERG EBOOK THE BROTHERS KARAMAZOV***
..........................................................................


= PART I

== The History Of A Family

=== Fyodor Pavlovitch Karamazov

Alexey Fyodorovitch Karamazov was the third son of Fyodor Pavlovitch'
);

INSERT INTO contents (asciidocId, `type`, doc) 
VALUES (
3, 
'ADOC', 
'= Source code listing
 
Code listings look cool with Asciidoctor and highlight.js with {highlightjs-theme} theme.
 
[source,groovy]
----
// File: User.groovy
class User {
String username
}
----
 
[source,sql]
----
CREATE TABLE USER (
ID INT NOT NULL,
USERNAME VARCHAR(40) NOT NULL
);
----'
);

INSERT INTO contents (asciidocId, `type`, doc) 
VALUES (
4, 
'ADOC', 
'= Source code listing2
 
Code listings look cool with Asciidoctor and highlight.js with {highlightjs-theme} theme.
 
[source,groovy]
----
// File: User.groovy
class User {
String username
}
----
 
[source,sql]
----
CREATE TABLE USER (
ID INT NOT NULL,
USERNAME VARCHAR(40) NOT NULL
);
----'
);
    
INSERT INTO contents (asciidocId, `type`, doc) 
VALUES (
1, 
'HTML', 
'<h1>Another Introduction to AsciiDocc</h1>'
);

INSERT INTO contents (asciidocId, `type`, doc) 
VALUES (
2, 
'HTML', 
'<h1>The History Of A Family</h1>'
);

INSERT INTO contents (asciidocId, `type`, doc) 
VALUES (
3, 
'HTML', 
'<h1>Source code listing</h1>'
);

INSERT INTO contents (asciidocId, `type`, doc) 
VALUES (
4, 
'HTML', 
'<h1>Source code listing2</h1>'
);
    
INSERT INTO categories (asciidocId, name) VALUES (1, 'test1');
INSERT INTO categories (asciidocId, name) VALUES (2, 'test1');
INSERT INTO categories (asciidocId, name) VALUES (3, 'test1');
INSERT INTO categories (asciidocId, name) VALUES (4, 'test2');