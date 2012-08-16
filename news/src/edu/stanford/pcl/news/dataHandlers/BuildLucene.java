package edu.stanford.pcl.news.dataHandlers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import edu.stanford.nlp.pipeline.Annotation;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: seanwestwood
 * Date: 8/16/12
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuildLucene {

    public static void addDocuments() throws IOException {


        Article article;
        Updater updater = new Updater();
        updater.connect();
        BasicDBObject query = new BasicDBObject();
        DBCursor cursor = updater.queryCursor("articles", query);

        Integer count = 0;
        while (cursor.hasNext()) {
            cursor.next();
            DBObject obj = cursor.curr();
            article = Article.fromMongoObject(obj);
            updater.createLucene(article);
            if (count > 5)
                break;
            count++;
            System.out.println(article.getMediaSource() + count);
        }
        updater.close();

    }

}
