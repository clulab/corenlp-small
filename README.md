# corenlp-small

Stripped down version of CoreNLP 3.9.2. 
The purpose of this repo is to be able to run SUTime without the overhead of the entire CoreNLP suite.

## License

This follows exactly the same license as the original CoreNLP: https://github.com/stanfordnlp/CoreNLP

## Changes

The following directories and files were removed from the original CoreNLP:

```
rm -rf src/main/java/edu/stanford/nlp/naturalli/demo
rm -rf src/main/java/edu/stanford/nlp/ie/ner/webapp
rm -rf src/main/java/edu/stanford/nlp/ie/ner/ui
rm -rf src/main/java/edu/stanford/nlp/pipeline/webapp
rm -rf src/main/java/edu/stanford/nlp/time/suservlet
rm -rf src/main/java/edu/stanford/nlp/trees/tregex/gui

rm -f src/main/java/edu/stanford/nlp/patterns/SPIEDServlet.java
rm -f src/main/java/edu/stanford/nlp/patterns/surface/PatternsForEachTokenLucene.java
rm -f src/main/java/edu/stanford/nlp/util/LuceneFieldType.java
rm -f src/main/java/edu/stanford/nlp/util/logging/SLF4JHandler.java
```


