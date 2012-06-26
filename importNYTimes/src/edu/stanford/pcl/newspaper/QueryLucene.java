package edu.stanford.pcl.newspaper;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.martiansoftware.jsap.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class QueryLucene {
    private static final String LUCENE_INDEX_DIRECTORY = "/rawdata/luceneindex";
    private ThreadLocal<Map<String, ArrayList>> results = new ThreadLocal<Map<String, ArrayList>>() {
        @Override
        protected Map<String, ArrayList> initialValue() {
            return new HashMap<String, ArrayList>();
        }
    };

    public void saveFile(String outputFile) throws IOException {
        Iterator<Entry<String, ArrayList>> iterator = results.get().entrySet().iterator();
        CSVWriter writer = new CSVWriter(new FileWriter(outputFile), '\t');

        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            ArrayList<String> row = (ArrayList) entry.getValue();
            String[] fullRow = row.toArray(new String[row.size()]);
            writer.writeNext(fullRow);
        }
        writer.close();
    }

    public String executeCountQuery(String[] searchTerms, String[] searchFields) throws IOException, ParseException {
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
        Directory index = new SimpleFSDirectory(new File(LUCENE_INDEX_DIRECTORY));
        IndexReader reader = IndexReader.open(index);
        Query query = MultiFieldQueryParser.parse(Version.LUCENE_30, searchTerms,searchFields, analyzer);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(0, true);
        searcher.search(query, collector);

        String count = String.valueOf(collector.getTotalHits());
        searcher.close();

        return count;
    }

    public static void main(String[] args) throws IOException, ParseException, JSAPException {
        SimpleJSAP jsap = new SimpleJSAP(
                "QueryLucene",
                "Pulls information from Lucene",
                new Parameter[]{
                        new FlaggedOption("queryListFile", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 'q', JSAP.NO_LONGFLAG,
                                "List of queries to run"),
                        new FlaggedOption("source", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 's', JSAP.NO_LONGFLAG,
                                "Sources to query"),
                        new FlaggedOption("outputFile", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 'o', JSAP.NO_LONGFLAG,
                                "Output file"),
                        new QualifiedSwitch("count", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'c', "count",
                                "Requests verbose output.").setList(true).setListSeparator(',')
                }
        );

        JSAPResult JSAPconfig = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);
        Boolean all = JSAPconfig.getString("source").equals("all");

        QueryLucene ql = new QueryLucene();
        //CSVReader CSVReader = new CSVReader(new FileReader(JSAPconfig.getString("queryListFile")), '\t');
        CSVReader CSVReader = new CSVReader(new FileReader("/Users/seanwestwood/Desktop/queryLucene.csv"), '\t');
        List<String[]> queries = CSVReader.readAll();

        for (String[] row : queries) {
            ArrayList<String> resultRow = new ArrayList<String>();
            String rowName = row[0];
            for (String column : row) {
                String source;
                String[] searchFields = {"text", "mediaSource", "overLap"};
                if ( all ){
                    source = "New York Times";
                    resultRow.add(ql.executeCountQuery(new String[] {column,source,"1"}, searchFields));
                    source = "Los Angeles Times";
                    resultRow.add(ql.executeCountQuery(new String[] {column,source,"1"}, searchFields));
                    source = "Baltimore Sun";
                    resultRow.add(ql.executeCountQuery(new String[] {column,source,"1"}, searchFields));
                    source = "Chicago Tribune";
                    resultRow.add(ql.executeCountQuery(new String[] {column,source,"1"}, searchFields));
                }
                else{
                    source = JSAPconfig.getString("source");
                    resultRow.add(ql.executeCountQuery(new String[] {column,source,"1"}, searchFields));
                }
            }
            ql.results.get().put(rowName, resultRow);
        }
        ql.saveFile(JSAPconfig.getString("outputFile"));
    }

}
