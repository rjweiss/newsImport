package edu.stanford.pcl.newspaper;

import com.mongodb.*;
import org.apache.lucene.index.IndexWriter;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class Updater {

    private static final String MONGO_DB_NAME = "news";
    private static final String MONGO_DB_ARTICLES_COLLECTION = "articles";
//    private static final String LUCENE_INDEX_DIRECTORY = "/rawdata/luceneindex";
//    private static final String MONGO_DB_MASTER_IP = "184.73.204.235";
//    private static final String MONGO_DB_SLAVE_IP = "107.22.253.110";

    private static Mongo m;
    private static DB db;

    public Updater(DBCollection collection, IndexWriter indexWriter) {
    }

    private static void MongoConnect() {
        try {
//            ArrayList<ServerAddress> address = new ArrayList<ServerAddress>();
//            address.add(new ServerAddress(MONGO_DB_MASTER_IP, 27017));
//            address.add(new ServerAddress(MONGO_DB_SLAVE_IP, 27017));
            m = new Mongo("localhost");
            db = m.getDB(MONGO_DB_NAME);

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

        while (cursor.hasNext()) {
            cursor.next();
            DBObject obj = cursor.curr();
            article = Article.fromMongoObject(obj);

            //Process Types
            if (processType.equals("annotations")) {
                article.addFeature("Annotations", annotator.getAnnotations(article.getText()));
                updateMongo(article, collection);
            } else if (processType.equals("labels")) {
                article.addFeature("Labels", annotator.getAnnotations(article.getText()));
            }
        }
        return (false);
    }

    private static void updateMongo(Article article, DBCollection collection) {
        BasicDBObject query = new BasicDBObject();

        try {
            //toMongoObject() needs to account for newly created fields.
            collection.update(query, new BasicDBObject("$push", article.toMongoObject()), true, true); //check those true flags
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    private static void addFields(ArrayList<String> fieldNames, DBCollection collection) {
//        Iterator iterator = fieldNames.iterator();
//        BasicDBObject query = new BasicDBObject();
//
//        while (iterator.hasNext()) {
//            BasicDBObject update = new BasicDBObject();
//            update.put(String.valueOf(iterator.next()), false);
//            collection.update(query, new BasicDBObject("$push", update), true, true);
//        }
//    }
//
//    private static void removeFields(ArrayList<String> fieldNames, DBCollection collection) {
//        Iterator iterator = fieldNames.iterator();
//        BasicDBObject query = new BasicDBObject();
//
//        while (iterator.hasNext()) {
//            BasicDBObject update = new BasicDBObject();
//            update.put(String.valueOf(iterator.next()), false);
//            collection.update(query, new BasicDBObject("$pull", update), true, true);
//        }
//    }

    public static void main(String[] args) throws UnknownHostException {
        MongoConnect();
        DBCollection coll = db.getCollection(MONGO_DB_ARTICLES_COLLECTION);
        BasicDBObject query = new BasicDBObject();

        Boolean temp = collectAndUpdateResults(coll, query, "annotations");
        System.out.println(temp);
    }
}


