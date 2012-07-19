package edu.stanford.pcl.newspaper;

import org.joda.time.DateTime;

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

    //create method clearArticle()
    //second levels: add get/set for annotations<A<A<S>>, features<A<S>>, labels<A<S>>

    public boolean isValid() {
	boolean valid = true;
	valid &= (pageNumber != null && !pageNumber.isEmpty());
	valid &= (headline != null && !headline.isEmpty());
	valid &= (text != null && !text.isEmpty());
	valid &= (publicationDate!= null);
	valid &= (fileName != null && !fileName.isEmpty());
	valid &= (mediaType != null && !mediaType.isEmpty());
	valid &= (mediaSource != null && !mediaSource.isEmpty());
	return valid;
    }
}
