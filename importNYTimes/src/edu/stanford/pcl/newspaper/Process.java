package edu.stanford.pcl.newspaper;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import edu.stanford.nlp.pipeline.Annotation;

/**
 * Created with IntelliJ IDEA.
 * User: seanwestwood
 * Date: 7/20/12
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Process {
    private static boolean processResults(DBCollection collection, BasicDBObject query, String processType) {
        Article article;
        DBCursor cursor = collection.find(query).batchSize(10);
        AnnotationExtractor annotator = new AnnotationExtractor("tokenize, ssplit, pos, lemma, ner");//, parse");
        Annotation document;
//        Map<String, Object> annotations = new HashMap<String, Object>();

        Updater updater;
        while (cursor.hasNext()) {
            cursor.next();
            DBObject obj = cursor.curr();
            article = Article.fromMongoObject(obj);
            document = annotator.getAnnotations(article.getText());

            //Process Types
            if (processType.equals("annotations")) {
                article.setAnnotation(new AnnotatedDocument(document));
            }
            updater.updateMongo(article, collection);
            System.out.println(article.getFileName());
        }
        return true;
    }
}
