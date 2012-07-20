package edu.stanford.pcl.newspaper;

import com.mongodb.ReflectionDBObject;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;

import java.util.ArrayList;
import java.util.List;

public class AnnotatedDocument extends ReflectionDBObject {
    List<AnnotatedToken> tokens = new ArrayList<AnnotatedToken>();
    List<String> entitiesText = new ArrayList<String>();

    public AnnotatedDocument(Annotation annotation) {
        List<CoreLabel> tokenList = annotation.get(CoreAnnotations.TokensAnnotation.class);
        String lastEntity = "";
        String currentEntityText = "";
        for (CoreLabel token : tokenList) {
            AnnotatedToken at = new AnnotatedToken(token);
            tokens.add(at);

            if( !("O").equals(at.entity) && at.entity.equals(lastEntity) ){
                currentEntityText += " " + at.text;
            }
            else if( !("O").equals(at.entity) && !at.entity.equals(lastEntity) && !currentEntityText.isEmpty() ){
                entitiesText.add(currentEntityText);
                System.out.println(currentEntityText);
                currentEntityText ="";
            }
            lastEntity = at.entity;
        }
    }
}
