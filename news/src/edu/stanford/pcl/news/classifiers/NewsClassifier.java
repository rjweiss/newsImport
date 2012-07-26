package edu.stanford.pcl.news.classifiers;

import com.mongodb.*;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.LinearClassifierFactory;
import edu.stanford.nlp.ling.BasicDatum;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;

import java.net.UnknownHostException;
import java.text.NumberFormat;
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

    private ArrayList<Datum<String, String>> trainingData;
    private ArrayList<Datum<String, String>> testData;
    private LinearClassifierFactory<String, String> factory;
    private LinearClassifier<String, String> classifier;
    private BasicDBObject subsetQuery;
    private List<String> includedFeatures;
    private String classificationLabel;

    private static void connect() {
        try {
//            ArrayList<ServerAddress> address = new ArrayList<ServerAddress>();
//            address.add(new ServerAddress(MONGO_DB_MASTER_IP, 27017));
//            address.add(new ServerAddress(MONGO_DB_SLAVE_IP, 27017));
//            mongo = new Mongo(address);
            mongo = new Mongo("localhost");
            db = mongo.getDB(MONGO_DB_NAME);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        articles = db.getCollection("articles");
    }
//
    private static Datum<String, String> makeDatum(List<String> features, String label) {

        BasicDatum<String, String> datum;
        if (label == null) {
            datum = new BasicDatum<String, String>(features);
        } else {
            datum = new BasicDatum<String, String>(features, label);
        }
        return datum;
    }

    private List<String> featureAttributes = new ArrayList<String>();
    private String labelAttribute;

    public NewsClassifier(List<String> featureAttributes, String labelAttribute) {
        this.featureAttributes = featureAttributes;
        this.labelAttribute = labelAttribute;
        this.trainingData =  new ArrayList<Datum<String, String>>();
        this.testData = new ArrayList<Datum<String, String>>();
        this.factory = new LinearClassifierFactory<String, String>();

    }

    private Object getFeature(Object object, String[] attributeParts, int attributeIndex) {
        if (attributeParts.length == 1) {
            return ((DBObject)object).get(attributeParts[0]);
        }
        if (object instanceof List) {
            // XXX  Should check that this is the last dotted attribute.  Fine for now.
            List<Object> list = new ArrayList<Object>();
            for (Object item : (List)object) {
                // XXX  Should come up with attribute syntax for following format "text+[feature1,feature2,...]".
                list.add(((DBObject)item).get("text") + "+" + ((DBObject)item).get(attributeParts[attributeIndex]));
            }
            return list;
        }
        else {
            return getFeature(((DBObject)object).get(attributeParts[attributeIndex]), attributeParts, attributeIndex + 1);
        }
    }

    private Object getFeature(Object object, String attributeName) {
        String[] parts = attributeName.split("\\.");
        return getFeature(object, parts, 0);
    }

    public void train(BasicDBObject subsetQuery, double testSamplePercentage) {
        // TODO: make datum, sample, train and classify test subset
        DBCursor cursor = articles.find(subsetQuery);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            List<String> features = new ArrayList<String>();
            for (String attribute : featureAttributes) {
                Object feature = getFeature(obj, attribute);
                if (feature instanceof String) {
                    features.add((String)feature);
                }
                else if (feature instanceof List) {
                    features.addAll((List<String>)feature);
                }
            }
            trainingData.add(makeDatum(features, (String)obj.get(labelAttribute)));
        }

        double numSamples = trainingData.size() / testSamplePercentage;
        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < numSamples; i++) {
            int sampleIndex = rand.nextInt(trainingData.size());
            Datum sample = trainingData.get(sampleIndex);
            if (testData.contains(sample)) {
                i--;
                continue;
            }
            testData.add(sample);
            trainingData.remove(sampleIndex); //need to make sure this carries into actual trainingData
        }

        factory.useConjugateGradientAscent();
        factory.setVerbose(true);
        factory.setSigma(10.0);
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
//        int numClasses = 0;
//        double microAccuracy = 0.0;
//        double macroF1 = 0.0;

        for (String key : totalLabels) {
//            numClasses++;
            int tp = (int) contingency.getCount(key + "|TP");
            int fn = (int) contingency.getCount(key + "|FN");
            int fp = (int) contingency.getCount(key + "|FP");
            int tn = (int) contingency.getCount(key + "|TN");
            double p = (tp == 0) ? 0.0 : ((double) tp) / (tp + fp);
            double r = (tp == 0) ? 0.0 : ((double) tp) / (tp + fn);
            double f = (p == 0.0 && r == 0.0) ? 0.0 : 2 * p * r / (p + r);
            double acc = ((double) tp + tn) / num;
//            macroF1 += f;
//            microAccuracy += tp;
            System.err.println("Cls " + key + ": TP=" + tp + " FN=" + fn + " FP=" + fp + " TN=" + tn + "; Acc " + nf.format(acc) + " P " + nf.format(p) + " R " + nf.format(r) + " F1 " + nf.format(f));
//            microAccuracy = microAccuracy / num;
//            macroF1 = macroF1 / numClasses;
//            nf.setMinimumFractionDigits(5);
//            nf.setMaximumFractionDigits(5);
//            System.err.println("Micro-averaged accuracy/F1: " + nf.format(microAccuracy));
//            System.err.println("Macro-averaged F1: " + nf.format(macroF1));
        }

        System.out.println("Done.");
    }

    public void classify(BasicDBObject subsetQuery) {
        // TODO: call classifier on records of subset
    }

   public static void classifyNews() throws Exception {
        connect();
        BasicDBObject query = new BasicDBObject();

        List<String> includedFeatures = new ArrayList<String>();

//        NewsClassifier classifier = new NewsClassifier();
//        classifier.classify();
//        makeTrainingData(DBCollection collection, BasicDBObject query, ArrayList<String> includedFeatures, String labelName) throws Exception {
//        makeTrainingData();


    }
    public static void main(String[] args) throws Exception {
        connect();

        List<String> featureAttributes = new ArrayList<String>();
//        featureAttributes.add("text");
//        featureAttributes.add("annotation.tokens.pos");
        featureAttributes.add("annotation.tokens.lemma.pos");
        String labelAttribute = "mediaSource";

        NewsClassifier c = new NewsClassifier(featureAttributes, labelAttribute);
        c.train(new BasicDBObject(), 10.0);
//        c.classify(new BasicDBObject());


    }

}