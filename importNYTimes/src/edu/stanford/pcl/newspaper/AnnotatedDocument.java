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
        String lastEntity = "O";
        String currentEntityText = "";
        for (CoreLabel token : tokenList) {
            AnnotatedToken at = new AnnotatedToken(token);
            tokens.add(at);

            if (!("O").equals(lastEntity)) {
                if (at.entity.equals(lastEntity)) {
                    currentEntityText += " " + at.text;
                } else if (!at.entity.equals(lastEntity) && !currentEntityText.isEmpty()) {
                    if (("TIME").equals(lastEntity)) {
                        entitiesTime.add(currentEntityText);
                    } else if (("LOCATION").equals(lastEntity)) {
                        entitiesLocation.add(currentEntityText);
                    } else if (("ORGANIZATION").equals(lastEntity)) {
                        entitiesOrganization.add(currentEntityText);
                    } else if (("PERSON").equals(lastEntity)) {
                        System.out.println("p2: " + currentEntityText);
                        entitiesPerson.add(currentEntityText);
                    } else if (("MONEY").equals(lastEntity)) {
                        entitiesMoney.add(currentEntityText);
                    } else if (("PERCENT").equals(lastEntity)) {
                        entitiesPercent.add(currentEntityText);
                    } else if (("DATE").equals(lastEntity)) {
                        entitiesDate.add(currentEntityText);
                    } else if (("MISC").equals(lastEntity)) {
                        entitiesMisc.add(currentEntityText);
                    }

                    //System.out.println(currentEntityText);
                    currentEntityText = at.text;
                }
            }
            lastEntity = at.entity;

        }
    }
}
