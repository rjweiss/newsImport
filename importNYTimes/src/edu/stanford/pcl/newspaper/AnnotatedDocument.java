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
        String lastEntityType = "O";
        String fullEntityName = "";
        for (CoreLabel token : tokenList) {
            AnnotatedToken at = new AnnotatedToken(token);
            tokens.add(at);

            if (!lastEntityType.equals("O")) {
                if (at.entity.equals(lastEntityType)) {
                    fullEntityName += " " + at.text;
                } else if (!at.entity.equals(lastEntityType) && !fullEntityName.isEmpty()) {
                    System.out.println(at.entity + " last: " + lastEntityType + " full: " + fullEntityName + " curr text: " + at.text);
                    if (lastEntityType.equals("TIME")) {
                        entitiesTime.add(fullEntityName);
                    } else if (lastEntityType.equals("LOCATION")) {
                        entitiesLocation.add(fullEntityName);
                    } else if (lastEntityType.equals("ORGANIZATION")) {
                        entitiesOrganization.add(fullEntityName);
                    } else if (lastEntityType.equals("PERSON")) {

                        entitiesPerson.add(fullEntityName);
                    } else if (lastEntityType.equals("MONEY")) {
                        entitiesMoney.add(fullEntityName);
                    } else if (lastEntityType.equals("PERCENT")) {
                        entitiesPercent.add(fullEntityName);
                    } else if (lastEntityType.equals("DATE")) {
                        entitiesDate.add(fullEntityName);
                    } else if (lastEntityType.equals("MISC")) {
                        entitiesMisc.add(fullEntityName);
                    }

                    System.out.println("balh");
                    fullEntityName = at.text;
                }
                else
                {
                    fullEntityName = at.text;
                }
            }
            else
            {
                fullEntityName = "";
            }

            lastEntityType = at.entity;

        }
    }
}
