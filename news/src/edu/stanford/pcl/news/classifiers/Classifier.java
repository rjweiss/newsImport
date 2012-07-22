package edu.stanford.pcl.newspaper;

import com.mongodb.*;
import edu.stanford.nlp.ling.BasicDatum;
import edu.stanford.nlp.ling.Datum;

import java.net.UnknownHostException;
import java.util.*;

//import java.io.Serializable;
//implement serializable to dump the trained classifier (eventually)

public class NewsClassifier {

    private static Mongo mongo;
    private static DB db;
    private static DBCollection articles;

    private static final String MONGO_DB_NAME = "news";
    private static final String MONGO_DB_ARTICLES_COLLECTION = "articles";
//    private static final String LUCENE_INDEX_DIRECTORY = "/rawdata/luceneindex";
//    private static final String MONGO_DB_MASTER_IP = "184.73.204.235";
//    private static final String MONGO_DB_SLAVE_IP = "107.22.253.110";

//    private NewsClassifier() {
//    }

    private static void MongoConnect() {
        try {
//            ArrayList<ServerAddress> address = new ArrayList<ServerAddress>();
//            address.add(new ServerAddress(MONGO_DB_MASTER_IP, 27017));
//            address.add(new ServerAddress(MONGO_DB_SLAVE_IP, 27017));
            mongo = new Mongo("localhost");
            db = mongo.getDB(MONGO_DB_NAME);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        articles = db.getCollection("articles");
    }
//
    private Datum<String, String> makeDatum(List<String> features, String label) {

        BasicDatum<String, String> datum;
        if (label == null) {
            datum = new BasicDatum<String, String>(features);
        } else {
            datum = new BasicDatum<String, String>(features, label);
        }
        return datum;
    }

    private ArrayList<Datum<String, String>> makeTrainingData(DBCollection collection, BasicDBObject query, ArrayList<String> includedFeatures, String labelName) throws Exception {
        List<String> features = new ArrayList<String>();

        DBCursor cursor = articles.find(query);

        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            BasicDBObject annotation = (BasicDBObject) obj.get("annotation");
            BasicDBList tokenList = (BasicDBList) annotation.get("tokens");

            for (int i = 0; i < tokenList.size(); i++) {
                DBObject token = (DBObject) tokenList.get(i);
                StringBuilder sb = new StringBuilder();
                for (String feature : (ArrayList<String>) includedFeatures) {
                    sb.append(token.get(feature)).append(" ");
                    features.add(sb.toString());
                }
            }
            System.out.println(features);
        }
        ArrayList<Datum<String, String>> trainingData = new ArrayList<Datum<String, String>>();
        trainingData.add(makeDatum(features, labelName));
        return trainingData;

    }
//
//    private void classify() throws Exception {
//        Mongo mongo = new Mongo();
//        DB db = mongo.getDB("news");
//        DBCollection articles = db.getCollection("articles");
//        BasicDBObject query = new BasicDBObject();
//        DBCursor cursor = articles.find(query);
//        ArrayList<Datum<String, String>> trainingData = new ArrayList<Datum<String, String>>();
//
//        int n = 1;
//        while (cursor.hasNext()) {
//            DBObject obj = cursor.next();
//            String currentHeadline = obj.get("headline").toString();
//            String currentSource = obj.get("mediaSource").toString();
//            trainingData.add(makeDocDatum(currentHeadline, currentSource));
//            System.out.println("Training " + (n++));
//        }
//
//        ArrayList<Datum<String, String>> testData = new ArrayList<Datum<String, String>>();
//
//        //should sample from same time period, e.g. train on first 3 weeks, test on last week, then cross validate
//
//        int numSamples = trainingData.size() / 25;
//        Random rand = new Random(System.currentTimeMillis());
//        for (int i = 0; i < numSamples; i++) {
//            int sampleIndex = rand.nextInt(trainingData.size());
//            Datum sample = trainingData.get(sampleIndex);
//            if (testData.contains(sample)) {
//                // Already sampled this datum, try again.
//                i--;
//                continue;
//            }
//            testData.add(sample);
//            trainingData.remove(sampleIndex);
//        }
//
//        LinearClassifierFactory<String, String> factory = new LinearClassifierFactory<String, String>();
//        factory.useConjugateGradientAscent();
//        factory.setVerbose(true);
//        factory.setSigma(10.0);
//        LinearClassifier<String, String> classifier = factory.trainClassifier(trainingData);
//
//        Counter<String> contingency = new ClassicCounter<String>();
//        Iterator<Datum<String, String>> iterator = testData.iterator();
//
//        while (iterator.hasNext()) {
//            Datum<String, String> datum = iterator.next();
//
//            Object goldAnswer = ((List) datum.labels()).get(0);
//            Object clAnswer = classifier.classOf(datum);
//
//            //taken from ColumnDataClassifier.java (StanfordCoreNLP)
//            for (String next : classifier.labels()) {
//                if (next.equals(goldAnswer)) {
//                    if (next.equals(clAnswer)) {
//                        contingency.incrementCount(next + "|TP");
//                    } else {
//                        contingency.incrementCount(next + "|FN");
//                    }
//                } else {
//                    if (next.equals(clAnswer)) {
//                        contingency.incrementCount(next + "|FP");
//                    } else {
//                        contingency.incrementCount(next + "|TN");
//                    }
//                }
//            }
//        }
//
//        //adapted from ColumnDataClassifier.java (StanfordCoreNLP)
//
//        Collection<String> totalLabels = classifier.labels();
//        NumberFormat nf = NumberFormat.getNumberInstance();
//        nf.setMinimumFractionDigits(3);
//        nf.setMaximumFractionDigits(3);
//        int num = testData.size();
//        int numClasses = 0;
//        double microAccuracy = 0.0;
//        double macroF1 = 0.0;
//
//        for (String key : totalLabels) {
//            numClasses++;
//            int tp = (int) contingency.getCount(key + "|TP");
//            int fn = (int) contingency.getCount(key + "|FN");
//            int fp = (int) contingency.getCount(key + "|FP");
//            int tn = (int) contingency.getCount(key + "|TN");
//            double p = (tp == 0) ? 0.0 : ((double) tp) / (tp + fp);
//            double r = (tp == 0) ? 0.0 : ((double) tp) / (tp + fn);
//            double f = (p == 0.0 && r == 0.0) ? 0.0 : 2 * p * r / (p + r);
//            double acc = ((double) tp + tn) / num;
//            macroF1 += f;
//            microAccuracy += tp;
//            System.err.println("Cls " + key + ": TP=" + tp + " FN=" + fn + " FP=" + fp + " TN=" + tn + "; Acc " + nf.format(acc) + " P " + nf.format(p) + " R " + nf.format(r) + " F1 " + nf.format(f));
//            microAccuracy = microAccuracy / num;
//            macroF1 = macroF1 / numClasses;
//            nf.setMinimumFractionDigits(5);
//            nf.setMaximumFractionDigits(5);
//            System.err.println("Micro-averaged accuracy/F1: " + nf.format(microAccuracy));
//            System.err.println("Macro-averaged F1: " + nf.format(macroF1));
//        }
//
//        System.out.println("Done.");
//    }

    public static void main(String[] args) throws Exception {
        MongoConnect();
        BasicDBObject query = new BasicDBObject();

        List<String> includedFeatures = new ArrayList<String>();

//        NewsClassifier classifier = new NewsClassifier();
//        classifier.classify();
        makeTrainingData(DBCollection collection, BasicDBObject query, ArrayList<String> includedFeatures, String labelName) throws Exception {
        makeTrainingData();


    }

}