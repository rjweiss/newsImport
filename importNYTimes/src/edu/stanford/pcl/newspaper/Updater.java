package edu.stanford.pcl.newspaper;

import com.mongodb.*;
import edu.stanford.nlp.pipeline.Annotation;
import org.apache.lucene.index.IndexWriter;

import java.io.ByteArrayOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Updater {

    private static final String MONGO_DB_NAME = "news";
    private static final String MONGO_DB_ARTICLES_COLLECTION = "articles";
//    private static final String LUCENE_INDEX_DIRECTORY = "/rawdata/luceneindex";
//    private static final String MONGO_DB_MASTER_IP = "184.73.204.235";
//    private static final String MONGO_DB_SLAVE_IP = "107.22.253.110";

    private static Mongo mongo;
    private static DB db;


    public Updater(DBCollection collection, IndexWriter indexWriter) {
    }

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
    }

    private static ArrayList<DBObject> getQueryResults(DBCollection collection, BasicDBObject query) {
        ArrayList<DBObject> queryResults = new ArrayList<DBObject>();
        DBCursor cursor = collection.find(query).batchSize(10);
        while (cursor.hasNext()) {
            cursor.next();
            DBObject obj = cursor.curr();
            queryResults.add(obj);
        }
        return (queryResults);
    }

    private static boolean collectAndUpdateResults(DBCollection collection, BasicDBObject query, String processType) {
        Article article;
        DBCursor cursor = collection.find(query).batchSize(10);
        AnnotationExtractor annotator = new AnnotationExtractor("tokenize, ssplit, pos, lemma, ner");
        Annotation document;
        Map<String, Annotation> annotations = new HashMap<String, Annotation>();
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;


        while (cursor.hasNext()) {
            cursor.next();
            DBObject obj = cursor.curr();
            article = Article.fromMongoObject(obj);
            document = annotator.getAnnotations(article.getText());


            //Process Types
            if (processType.equals("annotations")) {
                annotations.put("annotations", document);
                article.setFeatures(annotations);
            }
            updateMongo(article, collection);
            System.out.println(article.getFileName().toString());

        }
        return true;
    }

    private static void updateMongo(Article article, DBCollection collection) {
        BasicDBObject query = new BasicDBObject();

        try {
            collection.update(query, new BasicDBObject("$push", article.toMongoObject()), true, true); //check those true flags
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        MongoConnect();
        DBCollection coll = db.getCollection(MONGO_DB_ARTICLES_COLLECTION);
        BasicDBObject query = new BasicDBObject();

        Boolean annotated = collectAndUpdateResults(coll, query, "annotations");

        System.out.println("Collection annotated: " + annotated);
    }
}


