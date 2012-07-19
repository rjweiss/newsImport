package edu.stanford.pcl.newspaper;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class AnnotationExtractor {

    private StanfordCoreNLP pipeline;

    public AnnotationExtractor(String annotationsWanted) {

        Properties p = new Properties();
        p.put("annotators", annotationsWanted);
        pipeline = new StanfordCoreNLP(p);
    }

    public Annotation getAnnotations(String text) {

        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        return document;
    }
}
