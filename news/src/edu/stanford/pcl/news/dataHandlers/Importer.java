package edu.stanford.pcl.news.dataHandlers;

import com.mongodb.*;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.pcl.news.parsers.NytParser;
import edu.stanford.pcl.news.parsers.TribParser;
import edu.stanford.pcl.news.parsers.generalParser;
import org.apache.lucene.index.IndexWriter;

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
    private static AnnotationExtractor annotator = new AnnotationExtractor("tokenize, ssplit, pos, lemma, ner");//, parse");
    private static Annotation document;

    public Importer(DBCollection collection) {//}, IndexWriter indexWriter) {
        this.collection = collection;
        //this.indexWriter = indexWriter;
    }

    private static void MongoConnect() {
        try {
            ArrayList<ServerAddress> address = new ArrayList<ServerAddress>();
            address.add(new ServerAddress(MONGO_DB_MASTER_IP, 27017));
            //address.add(new ServerAddress(MONGO_DB_SLAVE_IP, 27017));
            mongo = new Mongo("184.73.204.235", 27017);
            db = mongo.getDB(MONGO_DB_NAME);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public int[] importAll(File path, String sourceName, String language, String country, String parserType) {

        int imported = 0;
        int skipped = 0;
        for (File file : path.listFiles()) {
            if (file.isDirectory()) {
                // Recursively import sub-directories. and stuff
                int[] result = importAll(file, sourceName, language, country, parserType);

                System.out.println(file.getAbsolutePath() + " (" + result[0] + ", " + result[1] + ")");
            } else {
                // TODO:  Only import XML files, and probably do some sanity checking.
                //System.out.println("Parsing " + file.getAbsolutePath() + "...");
                String extension = null;
                int dotPos = file.getName().lastIndexOf(".");
                extension = file.getName().substring(dotPos + 1);

                if ("xml".equals(extension)) {
                    //System.out.println(extension);
                    Article article;

                    try {
                        if (parserType.equals("NytParser")) {
                            article = new NytParser().parse(file, sourceName, language, country);
                        } else if (parserType.equals("TribParser")) {
                            article = new TribParser().parse(file, sourceName, language, country);
                        } else {
                            article = new generalParser().parse(file, sourceName, language, country);
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

                    if (article.getText().length() > 200) {

                        try {
                            document = annotator.getAnnotations(article.getText().replace("<p>", "").replace("</p>", ""));
                            article.setAnnotation(new AnnotatedDocument(document));
                        } catch (Exception e) {
                            skipped++;//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
                    }

                    /* try {
                        Document doc;
                        doc = article.toLuceneDocument();
                        indexWriter.addDocument(doc);
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }*/
                    imported++;

                }
            }
        }
        return new int[]{imported, skipped};
    }

    public static void importNews(String path, String sourceName, String language, String country, String parserType) throws IOException {
        // Connect to MongoDB.
        MongoConnect();

        db.getCollection(MONGO_DB_ARTICLES_COLLECTION).createIndex(new BasicDBObject("fileName", 1));

        /*   // Create/Open Lucene index.
   StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
   Directory index = new SimpleFSDirectory(new File(LUCENE_INDEX_DIRECTORY));
   IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
   IndexWriter indexWriter = new IndexWriter(index, config);*/

        // Recursively parse and import XML files...
        Importer importer = new Importer(db.getCollection(MONGO_DB_ARTICLES_COLLECTION));//, indexWriter);
        importer.importAll(new File(path), sourceName, language, country, parserType);

        // Clean up.
        //indexWriter.close();
        mongo.close();

        System.out.println("Done.");
    }

}
