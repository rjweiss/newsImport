package edu.stanford.pcl.newspaper;

import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.LinearClassifierFactory;
import edu.stanford.nlp.ling.BasicDatum;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

//import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


//implement serializable to dump the trained classifier (eventually)
public class NewsClassifier { //implements Serializable {

    private NewsClassifier() {
    }

    protected static Datum<String, String> makeDocDatum(String doc) {
        List<String> features = new ArrayList<String>();

        //we could probably pass POS tags and NEs as features if we wanted
        Properties p = new Properties();
        p.put("annotators", "tokenize, ssplit, pos, lemma, ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(p);

        Annotation document = new Annotation(doc);
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            for (CoreLabel token : tokens) {
                String currentPOS = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                String currentText = token.get(CoreAnnotations.TextAnnotation.class);
                String currentEntity = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                if ("NNP".equals(currentPOS) && !currentEntity.equals("O")) {
                    features.add(currentPOS + "+" + currentEntity + "=" + currentText);
                } else {
                    features.add(currentPOS + "=" + currentText);
                }
            }
        }

        //just because I don't have actual labels
        String label = new String(String.valueOf(doc.contains("Obama")));
        return new BasicDatum<String, String>(features, label);
    }

    public static void main(String[] args) throws Exception {

        ArrayList docs = new ArrayList();

        String doc1 = "Obama is a socialist";
        String doc2 = "Obama is a Democrat";
        String doc3 = "Romney is a socialist";
        String doc4 = "Romney is a Republican";
        String doc5 = "Romney is a communist";
        String doc6 = "Obama believes in angels";
        String doc7 = "Romney believes in angels";
        String doc8 = "Obama pretended to believe in angels so that he'd win the election";
        String doc9 = "Romney pretended to believe in angels so that he'll win the election";
        String doc10 = "Romney is a Democrat";

        docs.add(doc1);
        docs.add(doc2);
        docs.add(doc3);
        docs.add(doc4);
        docs.add(doc5);
        docs.add(doc6);
        docs.add(doc7);
        docs.add(doc8);
        docs.add(doc9);
        docs.add(doc10);

        Iterator iterator = docs.iterator();

        List<Datum<String, String>> trainingData = new ArrayList<Datum<String, String>>();

        //training data
        while (iterator.hasNext()) {
            String doc = iterator.next().toString();
            trainingData.add(makeDocDatum(doc));
        }

        //test data
        Datum<String, String> obamaTrue = makeDocDatum("Obama is a socialist");
        Datum<String, String> obamaFalse = makeDocDatum("Paul is a socialist");

        // Build a classifier factory
        LinearClassifierFactory<String,String> factory = new LinearClassifierFactory<String,String>();
        factory.useConjugateGradientAscent();
        // Turn on per-iteration convergence updates
        factory.setVerbose(true);
        //Small amount of smoothing
        factory.setSigma(10.0);
        // Build a classifier
        LinearClassifier<String,String> classifier = factory.trainClassifier(trainingData);
//        NaiveBayesClassifier<String, String> classifier = factory.trainClassifier(trainingData);

        classifier.dump();
        // Test the classifier
        System.out.println("Obama instance got: " + classifier.classOf(obamaTrue));
//        classifier.justificationOf(obamaTrue);
        System.out.println("Other instance got: " + classifier.classOf(obamaFalse));
//        classifier.justificationOf(obamaFalse);

    }

}