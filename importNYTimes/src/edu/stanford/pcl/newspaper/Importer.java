package edu.stanford.pcl.newspaper;

import com.mongodb.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Importer {
    private DBCollection collection;
    private IndexWriter indexWriter;

    private static final String MONGO_DB_NAME = "news";
    private static final String MONGO_DB_ARTICLES_COLLECTION = "articles";
    private static final String MONGO_DB_MASTER_IP = "184.73.204.235";
    private static final String MONGO_DB_SLAVE_IP = "107.22.253.110";
    private static final String LUCENE_INDEX_DIRECTORY = "/rawdata/luceneindex";
//    private static final String LUCENE_INDEX_DIRECTORY = "/Users/Rebecca/Documents/research/stanford/pcl/computationalNews/newsImport";
//    private static final String ARTICLE_IMPORT_ROOT_DIRECTORY = "/Volumes/NEWSPAPER/nytimes/2001/01";

    private static Mongo mongo;
    private static DB db;

    public Importer(DBCollection collection, IndexWriter indexWriter) {
        this.collection = collection;
        this.indexWriter = indexWriter;
    }
    private static void MongoConnect() {
        try {
            ArrayList<ServerAddress> address = new ArrayList<ServerAddress>();
            address.add(new ServerAddress(MONGO_DB_MASTER_IP, 27017));
            address.add(new ServerAddress(MONGO_DB_SLAVE_IP, 27017));
            mongo = new Mongo(address);
            db = mongo.getDB(MONGO_DB_NAME);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public int[] importAll(File path, String source) {

        int imported = 0;
        int skipped = 0;
        for (File file : path.listFiles()) {
            if (file.isDirectory()) {
                // Recursively import sub-directories. and stuff
                int[] result = importAll(file, source);

                System.out.println(file.getAbsolutePath() + " (" + result[0] + ", " + result[1] + ")");
            } else {
                // TODO:  Only import XML files, and probably do some sanity checking.
                //System.out.println("Parsing " + file.getAbsolutePath() + "...");
                String extension=null;
                int dotPos = file.getName().lastIndexOf(".");
                extension = file.getName().substring(dotPos + 1);

                if ("xml".equals(extension)) {
                    //System.out.println(extension);
                    Article article;

                    try {
                        if ("New York Times".equals(source)) {
                            article = new NytParser().parse(file, source);
                        } else {
                            article = new TribParser().parse(file, source);
                        }
                    } catch (Exception e) {
                        // Parse failed, complain and skip.
                        e.printStackTrace(System.err);
                        skipped++;
                        continue;
                    }

                    if (article == null || !article.isValid()) {
                        System.err.println("Parse failed or article invalid: " + file.getAbsolutePath());
                        skipped++;
                        continue;
                    }

                    try {
                        BasicDBObject mongoObject;
                        mongoObject = article.toMongoObject();
                        collection.insert(mongoObject, WriteConcern.SAFE);
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                        continue;
                    }

                    try {
                        Document doc;
                        doc = article.toLuceneDocument();
                        indexWriter.addDocument(doc);
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                    imported++;

                }
            }
        }
        return new int[]{imported, skipped};
    }

    public static void main(String[] args) throws IOException {
        // Connect to MongoDB.
        MongoConnect();

        db.getCollection(MONGO_DB_ARTICLES_COLLECTION).createIndex(new BasicDBObject("fileName", 1));

        // Create/Open Lucene index.
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
        Directory index = new SimpleFSDirectory(new File(LUCENE_INDEX_DIRECTORY));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
        IndexWriter indexWriter = new IndexWriter(index, config);

        // Recursively parse and import XML files...
        Importer importer = new Importer(db.getCollection(MONGO_DB_ARTICLES_COLLECTION), indexWriter);
        importer.importAll(new File("/rawdata/newspapers/nytimes"), "New York Times");
        importer.importAll(new File("/rawdata/newspapers/chitrib"), "Chicago Tribune");
        importer.importAll(new File("/rawdata/newspapers/latimes"), "Los Angeles Times");

        importer.importAll(new File("/rawdata/newspapers/bsun"), "Baltimore Sun");

        // Clean up.
        indexWriter.close();
        mongo.close();

        System.out.println("Done.");
    }

}
