package edu.stanford.pcl.news.dataHandlers;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Article {
    private String id = "";
    private String pageNumber = "";
    private String headline = "";
    private String text = "";
    private DateTime publicationDate = new DateTime();
    private String fileName = "";
    private String mediaType = "";
    private String mediaSource = "";
    private String overLap = "";
    private String status = "";
    private String language = "";
    private String country = "";

    //private Map<String, Object> features;
    private AnnotatedDocument annotation;
    private Map<String, String> labels = new HashMap<String, String>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

/*    public void addFeature(String featureName) {
        this.features.put(featureName, null);
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }

    public Map<String, Object> getFeatures() {
        return features;
    }*/

    public AnnotatedDocument getAnnotation() {
        return annotation;
    }

    public void setAnnotation(AnnotatedDocument annotation) {
        this.annotation = annotation;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public void setLabel(String labelName, String labelValue) {
        this.labels.put(labelName, labelValue);
    }

    private static BasicDBList createMongoList(List<String> list, String keyName) {
        BasicDBList basicDBList = new BasicDBList();
        for (String entity : list) {
            DBObject t = new BasicDBObject();
            t.put(keyName, entity);
            basicDBList.add(t);
        }
        return basicDBList;
    }

    public BasicDBObject toMongoObject() {

        BasicDBObject obj = new BasicDBObject();
        if (this.getId() != null) {
            obj.put("_id", new ObjectId(this.getId()));
        }
        obj.put("pageNumber", this.getPageNumber());
        obj.put("publicationDate", this.getPublicationDate().toDate());
        obj.put("headline", this.getHeadline());
        obj.put("text", this.getText());
        obj.put("fileName", this.getFileName());
        obj.put("mediaType", this.getMediaType());
        obj.put("mediaSource", this.getMediaSource());
        obj.put("overLap", this.getOverLap());
        obj.put("status", this.getStatus());
        obj.put("language", this.getLanguage());
        obj.put("country", this.getCountry());

        // obj.put("features", this.getFeatures());

        //Time, Location, Organization, Person, Money, Percent, Date

        try {
            obj.put("entitiesTime", createMongoList(this.getAnnotation().entitiesTime, "time"));
        } catch (Exception e) {
            // e.printStackTrace();
        }
        try {
            obj.put("entitiesLocation", createMongoList(this.getAnnotation().entitiesLocation, "location"));
        } catch (Exception e) {
            //  e.printStackTrace();
        }
        try {
            obj.put("entitiesOrganization", createMongoList(this.getAnnotation().entitiesOrganization, "organization"));
        } catch (Exception e) {
            // e.printStackTrace();
        }
        try {
            obj.put("entitiesPerson", createMongoList(this.getAnnotation().entitiesPerson, "person"));
        } catch (Exception e) {
            // e.printStackTrace();
        }
        try {
            obj.put("entitiesMoney", createMongoList(this.getAnnotation().entitiesMoney, "money"));
        } catch (Exception e) {
            //  e.printStackTrace();
        }
        try {
            obj.put("entitiesPercent", createMongoList(this.getAnnotation().entitiesPercent, "percent"));
        } catch (Exception e) {
            //  e.printStackTrace();
        }
        try {
            obj.put("entitiesDate", createMongoList(this.getAnnotation().entitiesDate, "date"));
        } catch (Exception e) {
            //  e.printStackTrace();
        }
        try {
            obj.put("entitiesMisc", createMongoList(this.getAnnotation().entitiesMisc, "misc"));
        } catch (Exception e) {
            //  e.printStackTrace();
        }
        try {
            obj.put("entitiesCurrency", createMongoList(this.getAnnotation().entitiesCurrency, "currency"));
        } catch (Exception e) {
            //  e.printStackTrace();
        }
        try {
            obj.put("entitiesDuration", createMongoList(this.getAnnotation().entitiesDuration, "duration"));
        } catch (Exception e) {
            //  e.printStackTrace();
        }

        try {
            DBObject annotation = new BasicDBObject();
            BasicDBList list = new BasicDBList();
            for (AnnotatedToken token : this.annotation.tokens) {
                DBObject t = new BasicDBObject();
                t.put("text", token.text);
                t.put("lemma", token.lemma);
                t.put("pos", token.pos);
                t.put("entity", token.entity);
                list.add(t);
            }
            annotation.put("tokens", list);
            obj.put("annotation", annotation);
        } catch (Exception e) {
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            BasicDBList list = new BasicDBList();
            for (Map.Entry<String, String> entry : this.labels.entrySet()) {
                DBObject l = new BasicDBObject();
                String labelName = entry.getKey();
                String labelValue = entry.getValue();
                l.put("handLabeled", "True");
                l.put("classCategory", labelName);
                l.put("classValue", labelValue);
                list.add(l);
            }
            obj.put("labels", list);
        } catch (Exception e) {
            //e.printStackTrace()
        }
        return obj;
    }

    public static Article fromMongoObject(DBObject object) {
        Article article = new Article();
        DateTime dateTime = null;
        try {
            Date date = (Date) object.get("publicationDate");
            dateTime = new DateTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        article.setId(object.get("_id").toString());
        article.setHeadline(object.get("headline").toString());
        article.setPageNumber(object.get("pageNumber").toString());
        article.setText(object.get("text").toString());
        article.setFileName(object.get("fileName").toString());
        article.setLanguage(object.get("language").toString());
        article.setMediaSource(object.get("mediaSource").toString());
        article.setMediaType(object.get("mediaType").toString());
        article.setOverLap(object.get("overLap").toString());
        article.setPublicationDate(dateTime);
        article.setStatus(object.get("status").toString());
//        article.setCountry(object.get("country").toString());

        BasicDBObject annotation = (BasicDBObject) object.get("annotation");
        BasicDBList tokens = (BasicDBList) annotation.get("tokens");

        article.setAnnotation(new AnnotatedDocument());
        for (int i = 0; i < tokens.size(); i++) {
            BasicDBObject token = (BasicDBObject) tokens.get(Integer.toString(i));
            AnnotatedToken t = new AnnotatedToken();
            t.text = (String) token.get("text");
            t.lemma = (String) token.get("lemma");
            t.pos = (String) token.get("pos");
            t.entity = (String) token.get("entity");
            article.getAnnotation().tokens.add(t);
        }

        BasicDBList entitiesTime = null;
        try {
            entitiesTime = (BasicDBList) object.get("entitiesTime");
            for (int i = 0; i < entitiesTime.size(); i++) {
                BasicDBObject entity = (BasicDBObject) entitiesTime.get(Integer.toString(i));
                article.getAnnotation().entitiesTime.add(entity.get("time").toString());
            }
        } catch (Exception e) {
            //   e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            BasicDBList entitiesLocation = (BasicDBList) object.get("entitiesLocation");
            for (int i = 0; i < entitiesTime.size(); i++) {
                BasicDBObject entity = (BasicDBObject) entitiesLocation.get(Integer.toString(i));
                article.getAnnotation().entitiesLocation.add(entity.get("location").toString());
            }
        } catch (Exception e) {
            //   e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            BasicDBList entitiesOrganization = (BasicDBList) object.get("entitiesOrganization");
            for (int i = 0; i < entitiesTime.size(); i++) {
                BasicDBObject entity = (BasicDBObject) entitiesOrganization.get(Integer.toString(i));
                article.getAnnotation().entitiesOrganization.add(entity.get("organization").toString());
            }
        } catch (Exception e) {
            //  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            BasicDBList entitiesPerson = (BasicDBList) object.get("entitiesPerson");
            for (int i = 0; i < entitiesTime.size(); i++) {
                BasicDBObject entity = (BasicDBObject) entitiesPerson.get(Integer.toString(i));
                article.getAnnotation().entitiesPerson.add(entity.get("person").toString());
            }
        } catch (Exception e) {
            //  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        try {
            BasicDBList entitiesMoney = (BasicDBList) object.get("entitiesMoney");
            for (int i = 0; i < entitiesTime.size(); i++) {
                BasicDBObject entity = (BasicDBObject) entitiesMoney.get(Integer.toString(i));
                article.getAnnotation().entitiesMoney.add(entity.get("money").toString());
            }
        } catch (Exception e) {
            //  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            BasicDBList entitiesPercent = (BasicDBList) object.get("entitiesPercent");
            for (int i = 0; i < entitiesTime.size(); i++) {
                BasicDBObject entity = (BasicDBObject) entitiesPercent.get(Integer.toString(i));
                article.getAnnotation().entitiesPercent.add(entity.get("person").toString());
            }
        } catch (Exception e) {
            //  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            BasicDBList entitiesDate = (BasicDBList) object.get("entitiesDate");
            for (int i = 0; i < entitiesTime.size(); i++) {
                BasicDBObject entity = (BasicDBObject) entitiesDate.get(Integer.toString(i));
                article.getAnnotation().entitiesDate.add(entity.get("date").toString());
            }
        } catch (Exception e) {
            //  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            BasicDBList entitiesMisc = (BasicDBList) object.get("entitiesMisc");
            for (int i = 0; i < entitiesTime.size(); i++) {
                BasicDBObject entity = (BasicDBObject) entitiesMisc.get(Integer.toString(i));
                article.getAnnotation().entitiesMisc.add(entity.get("misc").toString());
            }
        } catch (Exception e) {
            // e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            BasicDBList entitiesCurrency = (BasicDBList) object.get("entitiesCurrency");
            for (int i = 0; i < entitiesTime.size(); i++) {
                BasicDBObject entity = (BasicDBObject) entitiesCurrency.get(Integer.toString(i));
                article.getAnnotation().entitiesCurrency.add(entity.get("currency").toString());
            }
        } catch (Exception e) {
            // e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            BasicDBList entitiesDuration = (BasicDBList) object.get("entitiesDuration");
            for (int i = 0; i < entitiesTime.size(); i++) {
                BasicDBObject entity = (BasicDBObject) entitiesDuration.get(Integer.toString(i));
                article.getAnnotation().entitiesDuration.add(entity.get("duration").toString());
            }
        } catch (Exception e) {
            // e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


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
        doc.add(new Field("country", this.getCountry(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        try {
            doc.add(new Field("entitiesTime", this.getAnnotation().entitiesTime.toString(), Field.Store.YES, Field.Index.ANALYZED));
        } catch (Exception e) {
        }
        try {
            doc.add(new Field("entitiesLocation", this.getAnnotation().entitiesLocation.toString(), Field.Store.YES, Field.Index.ANALYZED));
        } catch (Exception e) {
        }
        try {
            doc.add(new Field("entitiesOrganization", this.getAnnotation().entitiesOrganization.toString(), Field.Store.YES, Field.Index.ANALYZED));
        } catch (Exception e) {
        }
        try {
            doc.add(new Field("entitiesPerson", this.getAnnotation().entitiesPerson.toString(), Field.Store.YES, Field.Index.ANALYZED));
        } catch (Exception e) {
        }
        try {
            doc.add(new Field("entitiesMoney", this.getAnnotation().entitiesMoney.toString(), Field.Store.YES, Field.Index.ANALYZED));
        } catch (Exception e) {
        }
        try {
            doc.add(new Field("entitiesPercent", this.getAnnotation().entitiesPercent.toString(), Field.Store.YES, Field.Index.ANALYZED));
        } catch (Exception e) {
        }
        try {
            doc.add(new Field("entitiesDate", this.getAnnotation().entitiesDate.toString(), Field.Store.YES, Field.Index.ANALYZED));
        } catch (Exception e) {
        }
        try {
            doc.add(new Field("entitiesMisc", this.getAnnotation().entitiesMisc.toString(), Field.Store.YES, Field.Index.ANALYZED));
        } catch (Exception e) {
        }
        try {
            doc.add(new Field("entitiesDuration", this.getAnnotation().entitiesDuration.toString(), Field.Store.YES, Field.Index.ANALYZED));
        } catch (Exception e) {
        }
        try {
            doc.add(new Field("entitiesCurrency", this.getAnnotation().entitiesCurrency.toString(), Field.Store.YES, Field.Index.ANALYZED));
        } catch (Exception e) {
        }

        /*  try {
            doc.add(new Field("labelHardsoft1", this.labels.get("hardsoft1"), Field.Store.YES, Field.Index.ANALYZED));
        } catch (Exception e) {
        }

        try {
            doc.add(new Field("labelHardsoft2", this.labels.get("hardsoft2"), Field.Store.YES, Field.Index.ANALYZED));
        } catch (Exception e) {
        }

        try {
            doc.add(new Field("labelHardsoft3", this.labels.get("hardsoft3"), Field.Store.YES, Field.Index.ANALYZED));
        } catch (Exception e) {
        }

        try {
            doc.add(new Field("labelTopic", this.labels.get("topic"), Field.Store.YES, Field.Index.ANALYZED));
        } catch (Exception e) {
        }

        try {
            doc.add(new Field("labelSentiment", this.labels.get("sentiment"), Field.Store.YES, Field.Index.ANALYZED));
        } catch (Exception e) {
        }*/

        return (doc);
    }

    public boolean isValid() {
        boolean valid = true;
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
            this.setCountry(null);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Clear fields failed");
            return false;
        }
        return true;
    }
}

