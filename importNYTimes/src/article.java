import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Rebecca
 * Date: 6/18/12
 * Time: 5:12 PM
 * To change this template use File | Settings | File Templates.
 */

public class article {

    private String articlePageNumber;
    private String articleHeadline;
    private String articleText;
    private String articlePublicationDate;
    private String articleFileName;
    private String articleContentType;
    private String articleContentSource;

    private DBCollection myColl;

    public void setArticleContentType(String s){
        articleContentType = s;
    }

    public String getArticleContentType(){
        return articleContentType;
    }

    public void setArticleContentSource(String s){
        articleContentSource = s;
    }

    public String getArticleContentSource(){
        return articleContentSource;
    }

    //article page number

    public void setArticlePageNumber(String s){
        articlePageNumber = s;
    }

    public String getArticlePageNumber(){
        return articlePageNumber;
    }

    //article headline
    public void setArticleHeadline(String s){
        articleHeadline = s;
    }
    public String getArticleHeadline(){
        return articleHeadline;
    }

    //article articleText
    public void setArticleText(String s){
        articleText = s;
    }
    public String getArticleText(){
        return articleText;
    }

    //article publication date
    public void setArticlePublicationDate(String s){
        articlePublicationDate = s;
    }
    public String getArticlePublicationDate(){
        return articlePublicationDate;
    }

    //article file
    public void setFile(String s){
        articleFileName = s;
    }
    public String getFile(){
        return articleFileName;
    }

    public void connect() throws Exception{

        Mongo myM = new Mongo();
        DB myDB = myM.getDB("test");
        myColl = myDB.getCollection("articles");

    }

    public void disconnect() throws Exception{

    }

    public void insert(){
        BasicDBObject doc = new BasicDBObject();

        doc.put("articlePageNumber", articlePageNumber);
        doc.put("articlePublicationDate", articlePublicationDate);
        doc.put("articleHeadline", articleHeadline);
        doc.put("articleText", articleText);
        doc.put("articleFileName", articleFileName);
        doc.put("articleContentType", articleContentType);
        doc.put("articleContentSource", articleContentSource);

        myColl.insert(doc);
    }

    private void modifyLucene(){
        Analyzer analyzer = new StandardAnalyzer();
        // create an index in /tmp/index, overwriting an existing one:
//        IndexModifier indexModifier = new IndexModifier("/rawdata/luceneidex", analyzer, true);
        IndexWriter indexWriter = new IndexWriter("/rawdata/luceneindex", analyzer, true)

        Document doc = new Document();

        doc.add(new Field("articleContentSource", articleContentSource, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("articleContentType", articleContentType, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("articleFileName", articleFileName, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("articleText", articleText, Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("articleHeadline", articleHeadline, Field.Store.YES, Field.Index.ANALYZED));

        indexWriter.addDocument(doc);
//        indexModifier.addDocument(doc);
        indexWriter.close();
//        indexModifier.close();
    }

    /*public static void buildIndex() throws IOException {
        IndexWriter indexWriter
                = new IndexWriter(FSDirectory.getDirectory("/rawdata/luceneindex/"),
                new StandardAnalyzer(),
                IndexWriter.MaxFieldLength.LIMITED);
        String[] texts = new String[] {  "hello world",
                "hello sailor",
                "goodnight moon" };
        for (String text : texts) {
            Document doc = new Document();
            doc.add(new Field("text",text,
                    Field.Store.YES,Field.Index.ANALYZED));
            indexWriter.addDocument(doc);
        }
        indexWriter.close();*/
    }
}
