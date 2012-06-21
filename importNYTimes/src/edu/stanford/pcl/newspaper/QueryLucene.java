import org.apache.lucene.analysis.standard.StandardAnalyzer;
 import org.apache.lucene.document.Document;
 import org.apache.lucene.document.Field;
 import org.apache.lucene.index.IndexReader;
 import org.apache.lucene.index.IndexWriter;
 import org.apache.lucene.index.IndexWriterConfig;
 import org.apache.lucene.queryParser.ParseException;
 import org.apache.lucene.queryParser.QueryParser;
 import org.apache.lucene.search.IndexSearcher;
 import org.apache.lucene.search.Query;
 import org.apache.lucene.search.ScoreDoc;
 import org.apache.lucene.search.TopScoreDocCollector;
 import org.apache.lucene.store.*;
 import org.apache.lucene.store.RAMDirectory;
 import org.apache.lucene.util.Version;
import java.io.*;
 
import java.io.IOException;
 
public class QueryLucene {
   private static final String LUCENE_INDEX_DIRECTORY = "/rawdata/luceneindex";
   public static void main(String[] args) throws IOException, ParseException {
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
        Directory index = new SimpleFSDirectory(new File(LUCENE_INDEX_DIRECTORY));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
        IndexWriter indexWriter = new IndexWriter(index, config);

 
 
    if (args.length < 2) {
	System.out.println("QueryLucene <attribute> <search-value>");
	System.exit(1);
    } 
     String attr = args[0];
     String value = args[1];
 
     Query q = new QueryParser(Version.LUCENE_36, attr, analyzer).parse(value);
 
     int hitsPerPage = 10;
     IndexReader reader = IndexReader.open(index);
     IndexSearcher searcher = new IndexSearcher(reader);
     TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
     searcher.search(q, collector);
     ScoreDoc[] hits = collector.topDocs().scoreDocs;
     
     System.out.println("");
     System.out.println("Top hits with \"" + value + "\" in the " + attr + ":");
     for(int i=0;i<hits.length;++i) {
       int docId = hits[i].doc;
       Document d = searcher.doc(docId);
       String text = d.get(attr);
       int trunc = 120;
       if (text.length() > trunc) text = text.substring(0, trunc) + "...";
       String pad = "  ";
       if (i < 9) pad += " ";
       System.out.println(pad + (i + 1) + ". " + text);
     }
     if (collector.getTotalHits() > hits.length) {
	System.out.println("  ... and " + (collector.getTotalHits() - hits.length) + " more.");
     }
 
    searcher.close();
   }
 
 }
