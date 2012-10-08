package edu.stanford.pcl.news.dataHandlers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.io.IOException;

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
            count++;
            System.out.println(article.getMediaSource() + count);
        }
        updater.close();
    }
}
