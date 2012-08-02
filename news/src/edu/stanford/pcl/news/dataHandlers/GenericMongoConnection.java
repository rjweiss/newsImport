package edu.stanford.pcl.news.dataHandlers;

import com.mongodb.*;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: seanwestwood
 * Date: 8/1/12
 * Time: 5:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenericMongoConnection {

    private static final String MONGO_DB_NAME = "news";

    private static Mongo mongo;
    private static DB db;


    public GenericMongoConnection() {
    }

    public void connect() throws IOException {
        try {
            mongo = new Mongo("184.73.204.235");
            db = mongo.getDB(MONGO_DB_NAME);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public DBObject getOne(String collectionName, BasicDBObject query) {
        DBCollection collection = db.getCollection(collectionName);
        DBObject doc = collection.findOne(query);
        return doc;
    }

    public DBCursor getFullSet(String collectionName, BasicDBObject query) {
        DBCollection collection = db.getCollection(collectionName);
        DBCursor cursor = collection.find(query);
        return cursor;
    }

    public void update(String collectionName, String ID, BasicDBObject updatedField) {
        DBCollection collection = db.getCollection(collectionName);

        collection.update(new BasicDBObject().append("fileName", ID), updatedField);
    }

    public void close() throws IOException {
        mongo.close();
    }
}
