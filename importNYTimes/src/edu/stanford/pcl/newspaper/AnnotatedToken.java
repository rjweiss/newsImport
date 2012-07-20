package edu.stanford.pcl.newspaper;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public class AnnotatedToken {
    String text;
    String lemma;
    String pos;
    String entity;

    public AnnotatedToken(CoreLabel token) {
        text = token.get(CoreAnnotations.TextAnnotation.class);
        lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
        pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
        entity = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    }

    public String toString() {
        return new StringBuilder()
                .append(text)
                .append(" [").append(lemma).append(";").append(pos).append(";").append(entity).append("]")
                .toString();
    }

}
