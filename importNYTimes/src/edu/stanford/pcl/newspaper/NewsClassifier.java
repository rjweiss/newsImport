package edu.stanford.pcl.newspaper;

import com.mongodb.*;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.LinearClassifierFactory;
import edu.stanford.nlp.ling.BasicDatum;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.util.CoreMap;

import java.text.NumberFormat;
import java.util.*;

//import java.io.Serializable;
//implement serializable to dump the trained classifier (eventually)

public class NewsClassifier {
    private StanfordCoreNLP pipeline;

    private NewsClassifier() {
        Properties p = new Properties();
//        p.put("annotators", "tokenize, ssplit, pos, lemma, ner");
//        p.put("annotators", "tokenize, ssplit");
        pipeline = new StanfordCoreNLP(p);
    }


    private Datum<String, String> makeDocDatum(String doc, String label) {
        List<String> features = new ArrayList<String>();

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

        BasicDatum<String, String> datum;
        if (label == null) {
            datum = new BasicDatum<String, String>(features);
        } else {
            datum = new BasicDatum<String, String>(features, label);
        }
        return datum;
    }

    private void classify() throws Exception {
        Mongo mongo = new Mongo();
        DB db = mongo.getDB("news");
        DBCollection articles = db.getCollection("articles");
        BasicDBObject query = new BasicDBObject();
//        query.put("mediaType", "Chicago Tribune");
        DBCursor cursor = articles.find(query);
        ArrayList<Datum<String, String>> trainingData = new ArrayList<Datum<String, String>>();

//        int trainMax = 1000;
        int n = 1;
        int m = 1;
        ArrayList nyt = new ArrayList();
        ArrayList ct = new ArrayList();
        ArrayList bs = new ArrayList();
        ArrayList lat = new ArrayList();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            String currentHeadline = obj.get("headline").toString();
            String currentSource = obj.get("mediaSource").toString();
            Datum datum = makeDocDatum(currentHeadline, currentSource);
//            Datum datum = null;
//            if (currentSource.equals("New York Times") && nyt.size() < 250) {
//                nyt.add(makeDocDatum(currentHeadline, currentSource));
//            }
//            else if (currentSource.equals("Chicago Tribune") && ct.size() < 250) {
//                ct.add(makeDocDatum(currentHeadline, currentSource));
//            }
//            else if (currentSource.equals("Baltimore Sun") && bs.size() < 250) {
//                bs.add(makeDocDatum(currentHeadline, currentSource));
//            }
//            else if (currentSource.equals("Los Angeles Times") && lat.size() < 250) {
//                lat.add(makeDocDatum(currentHeadline, currentSource));
//            }
            trainingData.add(datum);
            System.out.println("Training " + (n++));
        }
//        trainingData.addAll(nyt);
//        trainingData.addAll(ct);
//        trainingData.addAll(bs);
//        trainingData.addAll(lat);

//        for (Datum datum : trainingData)

//        while (cursor.hasNext()) {
//            DBObject obj = cursor.next();
//            String currentHeadline = obj.get("headline").toString();
//            String currentSource = obj.get("mediaSource").toString();
//            if ((currentSource.equals("New York Times") && m < 250) || ((currentSource.equals("Baltimore Sun") && m >= 250 && m < 500) || (currentSource.equals("Chicago Tribune") && m >= 500 && m < 750) || ((currentSource.equals("Los Angeles Times") && m >= 750)))) {
//                trainingData.add(makeDocDatum(currentHeadline, currentSource));
//                System.out.println("Training " + (n++));
//                if (n >= trainMax) break;
//            }
//            System.out.println(currentSource);
//            m++;
//        }


        ArrayList<Datum<String, String>> testData = new ArrayList<Datum<String, String>>();

        int numSamples = trainingData.size() / 10;
        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < numSamples; i++) {
            int sampleIndex = rand.nextInt(trainingData.size());
            Datum sample = trainingData.get(sampleIndex);
            if (testData.contains(sample)) {
                // Already sampled this datum, try again.
                i--;
                continue;
            }
            testData.add(sample);
        }

        // Build a classifier factory
        LinearClassifierFactory<String, String> factory = new LinearClassifierFactory<String, String>();
        factory.useConjugateGradientAscent();
        // Turn on per-iteration convergence updates
        factory.setVerbose(true);
        //Small amount of smoothing
        factory.setSigma(10.0);
        // Build a classifier
        LinearClassifier<String, String> classifier = factory.trainClassifier(trainingData);

        Counter<String> contingency = new ClassicCounter<String>();
        Iterator<Datum<String, String>> iterator = testData.iterator();

        while (iterator.hasNext()) {
            Datum<String, String> datum = iterator.next();

            Object goldAnswer = ((List) datum.labels()).get(0);
            Object clAnswer = classifier.classOf(datum);

            //taken from ColumnDataClassifier.java (StanfordCoreNLP)
            for (String next : classifier.labels()) {
                if (next.equals(goldAnswer)) {
                    if (next.equals(clAnswer)) {
                        contingency.incrementCount(next + "|TP");
                    } else {
                        contingency.incrementCount(next + "|FN");
                    }
                } else {
                    if (next.equals(clAnswer)) {
                        contingency.incrementCount(next + "|FP");
                    } else {
                        contingency.incrementCount(next + "|TN");
                    }
                }
            }
        }

        //adapted from ColumnDataClassifier.java (StanfordCoreNLP)

        Collection<String> totalLabels = classifier.labels();
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumFractionDigits(3);
        nf.setMaximumFractionDigits(3);
        int num = testData.size();
        int numClasses = 0;
        double microAccuracy = 0.0;
        double macroF1 = 0.0;

        for (String key : totalLabels) {
            numClasses++;
            int tp = (int) contingency.getCount(key + "|TP");
            int fn = (int) contingency.getCount(key + "|FN");
            int fp = (int) contingency.getCount(key + "|FP");
            int tn = (int) contingency.getCount(key + "|TN");
            double p = (tp == 0) ? 0.0 : ((double) tp) / (tp + fp);
            double r = (tp == 0) ? 0.0 : ((double) tp) / (tp + fn);
            double f = (p == 0.0 && r == 0.0) ? 0.0 : 2 * p * r / (p + r);
            double acc = ((double) tp + tn) / num;
            macroF1 += f;
            microAccuracy += tp;
            System.err.println("Cls " + key + ": TP=" + tp + " FN=" + fn + " FP=" + fp + " TN=" + tn + "; Acc " + nf.format(acc) + " P " + nf.format(p) + " R " + nf.format(r) + " F1 " + nf.format(f));
            microAccuracy = microAccuracy / num;
            macroF1 = macroF1 / numClasses;
            nf.setMinimumFractionDigits(5);
            nf.setMaximumFractionDigits(5);
            System.err.println("Micro-averaged accuracy/F1: " + nf.format(microAccuracy));
            System.err.println("Macro-averaged F1: " + nf.format(macroF1));
        }

        System.out.println("Done.");
    }

    public static void main(String[] args) throws Exception {
        NewsClassifier classifier = new NewsClassifier();
        classifier.classify();
    }

}