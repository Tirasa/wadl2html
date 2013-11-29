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

If pointing to [http://localhost:8888/schoolsoft.html]() you will see instead another example, featuring schema include.

### Use your own WADL

Pretty easy: 
 1. add your own WADL file as `src/main/resources/myfile.wadl`
 2. (optional) add any external XSD file as reported in `myfile.wadl`
 3. rebuild and relaunch (see above)
 4. access [http://localhost:8888/myfile.html]()
