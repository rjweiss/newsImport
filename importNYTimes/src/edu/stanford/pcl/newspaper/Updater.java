package edu.stanford.pcl.newspaper;

import com.mongodb.*;
import org.apache.lucene.index.IndexWriter;
import org.joda.time.DateTime;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Updater {

    private DBCollection collection;
//    private IndexWriter indexWriter;

    private static final String MONGO_DB_NAME = "news";
    private static final String MONGO_DB_ARTICLES_COLLECTION = "articles";
//    private static final String LUCENE_INDEX_DIRECTORY = "/rawdata/luceneindex";
//    private static final String MONGO_DB_MASTER_IP = "184.73.204.235";
//    private static final String MONGO_DB_SLAVE_IP = "107.22.253.110";

    private static Mongo m;
    private static DB db;

    public Updater(DBCollection collection, IndexWriter indexWriter) {
        this.collection = collection;
//        this.indexWriter = indexWriter;
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
    private static boolean collectResults(DBCollection collection, BasicDBObject query, String processType) {
        Article article = new Article();
        DBCursor cursor = collection.find(query).batchSize(10);
        while (cursor.hasNext()) {
            cursor.next();
            DBObject obj = cursor.curr();

            article.clearFields();
            
            //write method that takes DBObject and converts to Article
            article.setHeadline((String) obj.get("headline"));
            article.setPageNumber((String) obj.get("pageNumber"));
            article.setText((String) obj.get("text"));
            article.setFileName((String) obj.get("fileName"));
            article.setLanguage((String) obj.get("language"));
            article.setMediaSource((String) obj.get("mediaSource"));
            article.setMediaType((String) obj.get("mediaType"));
            article.setOverLap((String) obj.get("overLap"));
            article.setPublicationDate((DateTime) obj.get("publicationDate"));
            article.setStatus((String) obj.get("status"));

            //Process Types
            //

        }
        return (false);
    }



    //brokeass
    private static void updateMongoDocument(Article article, DBCollection collection, HashMap<String, String> newData, String newFieldToSet) {
        BasicDBObject query = new BasicDBObject();
        BasicDBObject mongoObject = new BasicDBObject();

        try {
            mongoObject.put("pageNumber", article.getPageNumber());
            mongoObject.put("publicationDate", article.getPublicationDate().toDate());
            mongoObject.put("headline", article.getHeadline());
            mongoObject.put("text", article.getText());
            mongoObject.put("fileName", article.getFileName());
            mongoObject.put("mediaType", article.getMediaType());
            mongoObject.put("mediaSource", article.getMediaSource());
            mongoObject.put("overLap", article.getOverLap());
            mongoObject.put("status", article.getStatus());
            mongoObject.put("language", article.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Field update failed");
            System.exit(1);
        }


        //shouldn't this be a switch based on article field case?
        try {
            BasicDBObject obj = new BasicDBObject();
            obj.put(newFieldToSet, newData);
            mongoObject.put(newFieldToSet, obj);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Field upsert failed");
            System.exit(1);
        }

        try {
            collection.update(query, new BasicDBObject("$push", mongoObject), true, true); //check those true flags
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Mongo update failed");
        }
    }

    private static void addFields(ArrayList<String> fieldNames, DBCollection collection) {
        Iterator iterator = fieldNames.iterator();
        BasicDBObject query = new BasicDBObject();

        while (iterator.hasNext()) {
            BasicDBObject update = new BasicDBObject();
            update.put(String.valueOf(iterator.next()), false);
            collection.update(query, new BasicDBObject("$push", update), true, true);
        }
    }

    private static void removeFields(ArrayList<String> fieldNames, DBCollection collection) {
        Iterator iterator = fieldNames.iterator();
        BasicDBObject query = new BasicDBObject();

        while (iterator.hasNext()) {
            BasicDBObject update = new BasicDBObject();
            update.put(String.valueOf(iterator.next()), false);
            collection.update(query, new BasicDBObject("$pull", update), true, true);
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        MongoConnect();
        DBCollection coll = db.getCollection(MONGO_DB_ARTICLES_COLLECTION);
        BasicDBObject query = new BasicDBObject();

        Boolean temp = collectResults(coll, query, null);


//        query.put("mediaSource", "New York Times");
//        ArrayList foo = getQueryResults(coll, query);

//        ArrayList<String> fields = new ArrayList<String>();
//
//        fields.add("Field1");
//        fields.add("Field2");
//
//        addFields(fields, coll);
//        updateFields(fields, coll);
        System.out.println(coll.findOne());

    }
}


