/**
 * Created with IntelliJ IDEA.
 * User: Rebecca
 * Date: 6/18/12
 * Time: 5:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class Article {

    public String pageNum;
    public String headline;
    public String text;
    public String pubDate;
    public String fileName;
    public String dirPath;
    public String contentType;
    public String contentSource;

    public Article(String type, String source){
        contentType = type;
        contentSource = source;
    }
    //article page number

    public void setPageNum(String s){
        pageNum = s;
    }

    public String getPageNum(){
        return pageNum;
    }

    //article headline
    public void setHeadline(String s){
        headline = s;
    }
    public String getHeadline(){
        return headline;
    }

    //article text
    public void setText(String s){
        text = s;
    }
    public String getText(){
        return text;
    }

    //article publication date
    public void setPubDate(String s){
        pubDate = s;
    }
    public String getPubDate(){
        return pubDate;
    }

    //article file
    public void setFile(String s){
        fileName = s;
    }
    public String getFile(){
        return fileName;
    }

    //article path
    public void setPath(String s){
        dirPath = s;
    }

    public String getPath(){
        return dirPath;
    }




}
