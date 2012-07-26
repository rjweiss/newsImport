package edu.stanford.pcl.news.dataHandlers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import edu.stanford.nlp.pipeline.Annotation;

import java.io.IOException;

public class Processor {


    public static void annotateUpdate(String collectionName, BasicDBObject query) throws IOException {
        Article article;

        AnnotationExtractor annotator = new AnnotationExtractor("tokenize, ssplit, pos, lemma, ner");//, parse");
        Annotation document;

        Updater updater = new Updater();
        updater.connect();
        DBCursor cursor = updater.queryCursor(collectionName, query);
        Integer count = 0;
        while (cursor.hasNext()) {
            cursor.next();
            DBObject obj = cursor.curr();
            article = Article.fromMongoObject(obj);

            document = annotator.getAnnotations(article.getText().replace("<p>", "").replace("</p>", ""));

            System.out.println(article.getFileName());

            article.setAnnotation(new AnnotatedDocument(document));

            updater.updateMongo(article, collectionName);
            //updater.updateLucene(article);

            count++;
            System.out.println(article.getMediaSource() + count);
        }
        updater.close();
    }

    public static void processNews(String mediaSource) throws IOException {
        BasicDBObject query = new BasicDBObject();
        //DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        //DateTime date = dateTimeFormatter.parseDateTime("2001-01-11");
        query.put("mediaSource", mediaSource);
        query.put("annotation", null);
        annotateUpdate("articles", query);
    }
}
