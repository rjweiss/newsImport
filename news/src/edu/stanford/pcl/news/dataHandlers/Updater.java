package edu.stanford.pcl.news.dataHandlers;

import com.mongodb.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import java.io.*;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Updater {

    private static final String MONGO_DB_NAME = "news";
    //private static final String LUCENE_INDEX_DIRECTORY = "/rawdata/luceneindex";
    private static final String MONGO_DB_MASTER_IP = "184.73.204.235";
    private static final String MONGO_DB_SLAVE_IP = "107.22.253.110";

    private static Mongo mongo;
    private static DB db;
    private static IndexWriter indexWriter;

    public Updater() {
    }

    public DBCursor queryCursor(String collectionName, BasicDBObject query) {
        DBCollection collection = db.getCollection(collectionName);
        DBCursor cursor = collection.find(query);
        return cursor;
    }

    public void connect() throws IOException {
        try {
            ArrayList<ServerAddress> address = new ArrayList<ServerAddress>();
            address.add(new ServerAddress(MONGO_DB_MASTER_IP, 27017));
            address.add(new ServerAddress(MONGO_DB_SLAVE_IP, 27017));
            mongo = new Mongo(address);
            db = mongo.getDB(MONGO_DB_NAME);
            // ReadPreference readPreference = ReadPreference.SECONDARY;
            // db.setReadPreference(readPreference);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        /*   try {
            StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
            Directory index = FSDirectory.open(new File(LUCENE_INDEX_DIRECTORY));
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
            indexWriter = new IndexWriter(index, config);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void batchAttributeUpdate(String collectionName, File batchFile) throws IOException {
//        DBCollection collection = db.getCollection(collectionName);
        BufferedReader reader = new BufferedReader(new FileReader(batchFile));
        String line = reader.readLine();

        String[] columnNames = line.split("\t");

        while ((line = reader.readLine()) != null) {
            String[] columnValues = line.split("\t");
//            String value;
            for (int i=0; i<columnValues.length; i++) {
                System.out.println(columnNames[i] + ": " + columnValues[i]);
            }

            // Got the record.
            
            System.out.println("");
        }
    }

    public void updateMongo(Article article, String collectionName) {
        DBCollection collection = db.getCollection(collectionName);
        BasicDBObject query = new BasicDBObject();

        try {
            collection.save(article.toMongoObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateLucene(Article article) throws IOException {
        Document document = article.toLuceneDocument();
        indexWriter.updateDocument(new Term("fileName", article.getFileName()), document);
    }

    public void close() throws IOException {
        mongo.close();
        // indexWriter.close();
    }

    public static void main(String[] args) throws IOException {
        File batchFile = new File("C:\\Users\\Rebecca\\Documents\\computationalMediaAnalysis\\data.txt");
        Updater update = new Updater();
        update.batchAttributeUpdate("foo", batchFile);
    }
}


