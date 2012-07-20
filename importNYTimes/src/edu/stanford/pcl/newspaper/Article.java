package edu.stanford.pcl.newspaper;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import edu.stanford.nlp.pipeline.Annotation;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Article {
    private String pageNumber;
    private String headline;
    private String text;
    private DateTime publicationDate;
    private String fileName;
    private String mediaType;
    private String mediaSource;
    private String overLap;
    private String status;
    private String language;

    private Map<String, Annotation> features;
    private ArrayList<String> labels;

    public Article() {
        features = null;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public DateTime getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(DateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaSource() {
        return mediaSource;
    }

    public void setMediaSource(String mediaType) {
        this.mediaSource = mediaType;
    }

    public String getOverLap() {
        return overLap;
    }

    public void setOverLap(String overLap) {
        this.overLap = overLap;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void addFeature(String featureName) {
        this.features.put(featureName, null);
    }

    public void setFeatures(Map<String, Annotation> features) {
        this.features = features;
    }

    public Map<String, Annotation> getFeatures() {
        return features;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public ArrayList<String> getLabels() {
        return labels;

    }

    public BasicDBObject toMongoObject() {

        BasicDBObject obj = new BasicDBObject();

        obj.put("pageNumber", this.getPageNumber());
        obj.put("publicationDate", this.getPublicationDate().toDate());//why is this toDate and not toDateTime?
        obj.put("headline", this.getHeadline());
        obj.put("text", this.getText());
        obj.put("fileName", this.getFileName());
        obj.put("mediaType", this.getMediaType());
        obj.put("mediaSource", this.getMediaSource());
        obj.put("overLap", this.getOverLap());
        obj.put("status", this.getStatus());
        obj.put("language", this.getStatus());
        obj.put("features", this.getFeatures());

        return obj;
    }

    public static Article fromMongoObject(DBObject object) {

        Article article = new Article();
        DateTime dateTime = null;
        try {
            Date date = (Date) object.get("publicationDate"); // something weird with jodatime and java date object
            dateTime = new DateTime(date);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        article.setHeadline((String) object.get("headline"));
        article.setPageNumber((String) object.get("pageNumber"));
        article.setText((String) object.get("text"));
        article.setFileName((String) object.get("fileName"));
        article.setLanguage((String) object.get("language"));
        article.setMediaSource((String) object.get("mediaSource"));
        article.setMediaType((String) object.get("mediaType"));
        article.setOverLap((String) object.get("overLap"));
        article.setPublicationDate((DateTime) dateTime);
        article.setStatus((String) object.get("status"));

//        if(object.containsField("features")){
//            article.setFeatures(( Map<String, Object>) object.get("features"));
//        }

        return article;
    }

    public Document toLuceneDocument() {
        Document doc = new Document();

        doc.add(new Field("pageNumber", this.getPageNumber(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new NumericField("publicationDate", 8, Field.Store.YES, true).setIntValue(Integer.parseInt(this.getPublicationDate().toString("yyyyMMdd"))));
        doc.add(new Field("publicationDateString", this.getPublicationDate().toString("yyyyMMdd"), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("headline", this.getHeadline(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("text", this.getText(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("fileName", this.getFileName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("mediaType", this.getMediaType(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("mediaSource", this.getMediaSource(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("overLap", this.getMediaType(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("status", this.getMediaType(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("language", this.getLanguage(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        return (doc);
    }

    //second levels: add get/set for annotations<A<A<S>>, features<A<S>>, labels<A<S>>

    public boolean isValid() {
        boolean valid = true;
        valid &= (pageNumber != null && !pageNumber.isEmpty());
        valid &= (headline != null && !headline.isEmpty());
        valid &= (text != null && !text.isEmpty());
        valid &= (publicationDate != null);
        valid &= (fileName != null && !fileName.isEmpty());
        valid &= (mediaType != null && !mediaType.isEmpty());
        valid &= (mediaSource != null && !mediaSource.isEmpty());
        return valid;
    }

    public boolean clearFields() {
        try {
            this.setFileName(null);
            this.setLanguage(null);
            this.setHeadline(null);
            this.setMediaSource(null);
            this.setMediaType(null);
            this.setOverLap(null);
            this.setPageNumber(null);
            this.setPublicationDate(null);
            this.setStatus(null);
            this.setText(null);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Clear fields failed");
            return false;
        }
        return true;
    }
}

