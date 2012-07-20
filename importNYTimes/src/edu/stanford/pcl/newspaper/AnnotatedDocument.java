package edu.stanford.pcl.newspaper;

import com.mongodb.ReflectionDBObject;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AnnotatedDocument extends ReflectionDBObject {
    List<AnnotatedToken> tokens = new ArrayList<AnnotatedToken>();
    List<String> entitiesTime = new ArrayList<String>();
    List<String> entitiesLocation = new ArrayList<String>();
    List<String> entitiesOrganization = new ArrayList<String>();
    List<String> entitiesPerson = new ArrayList<String>();
    List<String> entitiesMoney = new ArrayList<String>();
    List<String> entitiesPercent = new ArrayList<String>();
    List<String> entitiesDate = new ArrayList<String>();
    List<String> entitiesMisc = new ArrayList<String>();

    //Time, Location, Organization, Person, Money, Percent, Date



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
                if (("TIME").equals(at.entity)) {
                    entitiesTime.add(currentEntityText);
                }
                else if (("LOCATION").equals(at.entity)) {
                    entitiesLocation.add(currentEntityText);
                }
                else if (("ORGANIZATION").equals(at.entity)) {
                    entitiesOrganization.add(currentEntityText);
                }
                else if (("PERSON").equals(at.entity)) {
                    entitiesPerson.add(currentEntityText);
                }
                else if (("MONEY").equals(at.entity)) {
                    entitiesMoney.add(currentEntityText);
                }
                else if (("PERCENT").equals(at.entity)) {
                    entitiesPercent.add(currentEntityText);
                }
                else if (("DATE").equals(at.entity)) {
                    entitiesDate.add(currentEntityText);
                }
                else if (("MISC").equals(at.entity)) {
                    entitiesMisc.add(currentEntityText);
                }

                System.out.println(currentEntityText);
                currentEntityText = at.text;
            }
            lastEntity = at.entity;
        }
    }
}
