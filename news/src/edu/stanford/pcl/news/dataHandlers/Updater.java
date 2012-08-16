package edu.stanford.pcl.news.dataHandlers;

import com.mongodb.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Updater {

    private static final String MONGO_DB_NAME = "news";
    private static final String LUCENE_INDEX_DIRECTORY = "/rawdata/luceneData/englishNewspapers";

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

    public DBObject getOne(String collectionName, BasicDBObject query) {
        DBCollection collection = db.getCollection(collectionName);
        DBObject doc = collection.findOne(query);
        return doc;
    }

    public void connect() throws IOException {
        try {

            mongo = new Mongo("184.73.204.235", 27017);
            db = mongo.getDB(MONGO_DB_NAME);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
            Directory index = FSDirectory.open(new File(LUCENE_INDEX_DIRECTORY));
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
            indexWriter = new IndexWriter(index, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void batchAttributeUpdate(String collectionName, File batchFile, String fieldToUpdate, String idAttribute) throws IOException {
        DBCollection collection = db.getCollection(collectionName);
        BufferedReader reader = new BufferedReader(new FileReader(batchFile)); //change to CSVreader
        String line = reader.readLine();

        String[] columnNames = line.split("\t");

        while ((line = reader.readLine()) != null) {
            String[] columnValues = line.split("\t");
//            String value;
            Map<String, String> newLabels = new HashMap<String, String>();
            for (int i = 0; i < columnValues.length; i++) {
                System.out.println(columnNames[i] + ": " + columnValues[i]);
                newLabels.put(columnNames[i], columnValues[i]);
            }
            BasicDBObject query = new BasicDBObject();
            query.put(idAttribute, columnValues[0]);
            if (queryCursor(collectionName, query).hasNext()) {
                DBObject obj = queryCursor(collectionName, query).next();
                Article article = Article.fromMongoObject(obj);
                for (Map.Entry<String, String> entry : newLabels.entrySet()) {
                    if (!"fileName".equals(entry.getKey())) {
                        String labelName = entry.getKey();
                        String labelValue = entry.getValue();
                        article.setLabel(labelName, labelValue);
                    }
                }
                try {
                    collection.save(article.toMongoObject());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Inserted.");
            }
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
        try {
            Document document = article.toLuceneDocument();
            indexWriter.updateDocument(new Term("fileName", article.getFileName()), document);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public void createLucene(Article article) throws IOException {
        try {
            Document document = article.toLuceneDocument();
            indexWriter.addDocument(document);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }


    public void close() throws IOException {
        mongo.close();
        indexWriter.close();
    }
}


