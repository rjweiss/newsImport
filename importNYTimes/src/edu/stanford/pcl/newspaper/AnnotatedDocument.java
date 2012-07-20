package edu.stanford.pcl.newspaper;

import com.mongodb.ReflectionDBObject;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;

import java.util.ArrayList;
import java.util.List;

public class AnnotatedDocument extends ReflectionDBObject {
    List<AnnotatedToken> tokens = new ArrayList<AnnotatedToken>();
    public AnnotatedDocument(Annotation annotation) {
        List<CoreLabel> tokenList = annotation.get(CoreAnnotations.TokensAnnotation.class);
        for (CoreLabel token : tokenList) {
            tokens.add(new AnnotatedToken(token));
        }
    }
}
