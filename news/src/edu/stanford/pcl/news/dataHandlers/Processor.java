package edu.stanford.pcl.news.dataHandlers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import edu.stanford.nlp.pipeline.Annotation;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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

    public static void processNews(String mediaSource, String startDate, String endDate) throws IOException, ParseException {
        BasicDBObject query = new BasicDBObject();
        //DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        //DateTime date = dateTimeFormatter.parseDateTime("2001-01-11");

        if (mediaSource.equals("NewYorkTimes")) {
            mediaSource = "New York Times";
        } else if (mediaSource.equals("BaltimoreSun")) {
            mediaSource = "Baltimore Sun";
        } else if (mediaSource.equals("ChicagoTribune")) {
            mediaSource = "Chicago Tribune";

        } else if (mediaSource.equals("LosAngelesTimes")) {
            mediaSource = "Los Angeles Times";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        //System.out.println();
        // query.put("publicationDate", BasicDBObjectBuilder.start("$gte", dateFormat.parse(startDate).toString()).add("$lte", dateFormat.parse(endDate).toString()).get());
        query.put("mediaSource", mediaSource);
        query.put("annotation", null);
        annotateUpdate("articles", query);
    }
}
