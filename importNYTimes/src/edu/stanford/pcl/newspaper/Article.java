package edu.stanford.pcl.newspaper;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import edu.stanford.nlp.pipeline.Annotation;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.joda.time.DateTime;

import java.util.ArrayList;
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

    private Map<String, Object> features;
    private Annotation annotations;
    private ArrayList<String> labels;

    public Article() {
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

    public void addFeature(String featureName, Object value) {
        this.features.put(featureName, value);
    }

    //needed?
    public void setAnnotations(Annotation annotation) {
        this.annotations = annotation;
    }

    public Annotation getAnnotations() {
        return annotations;
    }


    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }

    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public ArrayList<String> getLabels() {
        return labels;

    }

    public DBObject toMongoObject(BasicDBObject mongoObject) {
        DBObject obj = new BasicDBObject();

        obj.put("pageNumber", this.getPageNumber());
        obj.put("publicationDate", this.getPublicationDate().toDate());
        obj.put("headline", this.getHeadline());
        obj.put("text", this.getText());
        obj.put("fileName", this.getFileName());
        obj.put("mediaType", this.getMediaType());
        obj.put("mediaSource", this.getMediaSource());
        obj.put("overLap", this.getOverLap());
        obj.put("status", this.getStatus());
        obj.put("language", this.getStatus());

        if(obj.containsField("annotations")){
            obj.put("annotations", this.getAnnotations());
        }

        return obj;
    }

    public static Article fromMongoObject(DBObject object) {
        Article article = new Article();
        article.setHeadline((String) object.get("headline"));
        article.setPageNumber((String) object.get("pageNumber"));
        article.setText((String) object.get("text"));
        article.setFileName((String) object.get("fileName"));
        article.setLanguage((String) object.get("language"));
        article.setMediaSource((String) object.get("mediaSource"));
        article.setMediaType((String) object.get("mediaType"));
        article.setOverLap((String) object.get("overLap"));
        article.setPublicationDate((DateTime) object.get("publicationDate"));
        article.setStatus((String) object.get("status"));
        return article;
    }

    public static Document toLuceneDocument(Article article) {
        Document doc = new Document();

        doc.add(new Field("pageNumber", article.getPageNumber(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new NumericField("publicationDate", 8, Field.Store.YES, true).setIntValue(Integer.parseInt(article.getPublicationDate().toString("yyyyMMdd"))));
        doc.add(new Field("publicationDateString", article.getPublicationDate().toString("yyyyMMdd"), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("headline", article.getHeadline(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("text", article.getText(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("fileName", article.getFileName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("mediaType", article.getMediaType(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("mediaSource", article.getMediaSource(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("overLap", article.getMediaType(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("status", article.getMediaType(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("language", article.getLanguage(), Field.Store.YES, Field.Index.NOT_ANALYZED));

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

