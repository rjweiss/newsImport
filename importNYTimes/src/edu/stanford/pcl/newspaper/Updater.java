package edu.stanford.pcl.newspaper;

import com.mongodb.*;
import edu.stanford.nlp.pipeline.Annotation;
import org.apache.lucene.index.IndexWriter;

import java.net.UnknownHostException;
import java.util.ArrayList;

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



    private static void updateMongo(Article article, DBCollection collection) {
        BasicDBObject query = new BasicDBObject();

        try {
            collection.save(article.toMongoObject());
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


