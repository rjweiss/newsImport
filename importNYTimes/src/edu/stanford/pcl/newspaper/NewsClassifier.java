package edu.stanford.pcl.newspaper;

import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.LinearClassifierFactory;
//import edu.stanford.nlp.classify.NaiveBayesClassifier;
import edu.stanford.nlp.ling.BasicDatum;
import edu.stanford.nlp.ling.Datum;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


public class NewsClassifier {

    private NewsClassifier() {
    }

    protected static Datum<String, String> makeDocDatum(String doc) {
        List<String> features = new ArrayList<String>();
        //we could probably pass POS tags and NEs as features if we wanted
        String[] words = doc.split("\\s+");
        for (String word : words){
            features.add(word);
        }
//        features.add(doc);
//        String[] words = doc.split("\\s+");
//        features.add(words);
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

        docs.add(doc1);
        docs.add(doc2);
        docs.add(doc3);
        docs.add(doc4);
        docs.add(doc5);
        docs.add(doc6);
        docs.add(doc7);
        docs.add(doc1);

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