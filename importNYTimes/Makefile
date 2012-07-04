
JARDIR := /home/ec2-user/jars
JARS := \
	mongo/mongo-2.7.3.jar \
	lucene/lucene-core-3.6.0.jar \
	opencsv/opencsv-2.3.jar \
	jsap/JSAP-2.1.jar \
	stanford/corenlp/joda-time.jar \

CLASSPATH := $(subst $(eval) ,:,$(addprefix $(JARDIR)/,$(JARS)))

SRCDIR := src

MAINCLASS := edu/stanford/pcl/newspaper/QueryLucene

MAINIMPORTCLASS := edu/stanford/pcl/newspaper/Importer

.PHONY: build run

build:
	javac -Xlint -cp $(CLASSPATH) -d out/ -sourcepath $(SRCDIR) $(SRCDIR)/$(MAINCLASS).java

import:

query:
	java -cp out:$(CLASSPATH) $(MAINIMPORTCLASS) ${ARGS}

