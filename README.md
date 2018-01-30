# GoAnnotationTool
Demo GO Annotation tool

This tool uses the new OLPG library forked from ontolib to parse GO. The purposes is to provide input to a widget that will take a list of human genes and display the most common GO annotations for the three main subhierarchies of GO. 

* Please contact Peter if you need access to any libraries.


To run the tool, do a maven install for OLPG (see that repo)
```
$ mvn install
```
Now go back to the cloned repo for GoAnnotationTool. Download the go.obo file and the goa_human.gaf.gz file from the Gene Ontology website http://www.geneontology.org/ into the same directory (otherwise adjust the paths). Unpack the gz file, make the app, and run it with the following commands
```
$ gunzip goa_human.gaf.gz
$ mvn clean package
$ java -jar target/goatool.jar go.obo goa_human.gaf 
```
You will see a list of GO terms with their number of annotated counts and their aspect.
* F: molecular function
* P: biological process
* C: cellular component

```
(...)
GO:0050900: 1: P
GO:0002250: 13: P
GO:0002377: 20: P
GO:0050776: 1: P
GO:0004252: 2: F
```

For the website essentially we need to pass the function that does this (which now just takes some random genes) the genes that are associated with some HPO term, and then retrieve the top 3 from F, P, and C.
