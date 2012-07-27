
JARDIR := /home/ec2-user/jars
JARS := \
mongo/mongo-2.7.3.jar \
        lucene/lucene-core-3.6.0.jar \
        opencsv/opencsv-2.3.jar \
        jsap/JSAP-2.1.jar \
        stanford/corenlp/stanford-corenlp-2012-04-09.jar \
        stanford/corenlp/stanford-corenlp-2012-04-09-models.jar \
        stanford/corenlp/xom.jar \
        stanford/corenlp/joda-time.jar \
        jsoup/jsoup-1.6.3.jar \

CLASSPATH := $(subst $(eval) ,:,$(addprefix $(JARDIR)/,$(JARS)))

SRCDIR := src

MAINCLASS := edu/stanford/pcl/news/NewsTools

.PHONY: build run

build:
	javac -Xlint -cp $(CLASSPATH) -d out/ -sourcepath $(SRCDIR) $(SRCDIR)/$(MAINCLASS).java

run:
	java -cp out:$(CLASSPATH) $(MAINCLASS)

query:
	java --cp out:$(CLASSPATH) $(MAINCLASS) ${ARGS}

