package edu.stanford.pcl.newspaper;

/**
 * Created by IntelliJ IDEA.
 * User: Rebecca
 * Date: 6/19/12
 * Time: 12:33 AM
 * To change this template use File | Settings | File Templates.
 */
// TODO:  Is this needed?
public class Article {
    private String pageNumber;  // TODO: Make this a number!
    private String headline;
    private String text;
    private String publicationDate;  // TODO: Make this a date!
    private String fileName;
    private String mediaType;
    private String mediaSource;
    private String overLap;
    private String status;
    // TODO:  File path!

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

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
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

    public boolean isValid() {
	boolean valid = true;
	valid &= (pageNumber != null && !pageNumber.isEmpty());
	valid &= (headline != null && !headline.isEmpty());
	valid &= (text != null && !text.isEmpty());
	valid &= (publicationDate!= null && !publicationDate.isEmpty());
	valid &= (fileName != null && !fileName.isEmpty());
	valid &= (mediaType != null && !mediaType.isEmpty());
	valid &= (mediaSource != null && !mediaSource.isEmpty());
	return valid;
    }
}
