wadl2html
=========

Builds an HTML browsable representation out of a Web Application Description Language (WADL) file.

Live demo of [Apache Syncope 1.2.0-SNAPSHOT RESTful API](http://people.apache.org/~ilgrosso/wadl2html/) is available.

### How to test

```
$ git clone git@github.com:Tirasa/wadl2html.git
$ cd wadl2html
$ mvn
```

Now poin your browser to [http://localhost:8888/]() - you should be able to browse 
[Apache Syncope](http://syncope.apache.org) RESTful API documentation.

### Use your own WADL

Pretty easy: just replace `src/main/resources/application.wadl` with your own, and relaunch as reported above.
