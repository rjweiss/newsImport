package edu.stanford.pcl.newspaper;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import edu.stanford.nlp.pipeline.Annotation;

import java.io.IOException;

public class Processor {


    public void annotateUpdate(String processType) throws IOException {
        Article article;

        AnnotationExtractor annotator = new AnnotationExtractor("tokenize, ssplit, pos, lemma, ner");//, parse");
        Annotation document;

        Updater updater = new Updater();
        updater.connect();
        DBCursor cursor = updater.queryCursor("articles");

        while (cursor.hasNext()) {
            cursor.next();
            DBObject obj = cursor.curr();
            article = Article.fromMongoObject(obj);
            document = annotator.getAnnotations(article.getText());

            //Process Types
            if (processType.equals("annotations")) {
                article.setAnnotation(new AnnotatedDocument(document));
            }
            updater.updateMongo(article, "articles");
            updater.updateLucene(article);
            System.out.println(article.getFileName());
        }
        updater.close();
    }
}
