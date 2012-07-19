package edu.stanford.pcl.newspaper;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class AnnotationExtractor {

    private StanfordCoreNLP pipeline;

    public AnnotationExtractor() {

        Properties p = new Properties();
        p.put("annotators", "tokenize, ssplit, pos, lemma, ner");
        pipeline = new StanfordCoreNLP(p);
    }

    public Annotation getAnnotations(String text) {

        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        return document;
    }
}
