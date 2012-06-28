package edu.stanford.pcl.newspaper;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.martiansoftware.jsap.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
        Query query = MultiFieldQueryParser.parse(Version.LUCENE_30, searchTerms, searchFields, analyzer);

        Query termQuery = parser.parse(termQueryString);

        Query pageQueryRange = NumericRangeQuery.newIntRange("page_count", 10, 20, true, true);

        Query query = termQuery.combine(new Query[]{termQuery, pageQueryRange});

        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(query, 1);
        String hitCount = String.valueOf(topDocs.totalHits);
        searcher.close();

        return hitCount;
    }

    public static ArrayList<String> createHeader(DateTime startDate, DateTime endDate) {
        ArrayList<String> resultHeader = new ArrayList<String>();
        for (DateTime date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            resultHeader.add(date.toString());
        }
        return resultHeader;
    }

    public static void main(String[] args) throws IOException, ParseException, JSAPException, java.text.ParseException {
        SimpleJSAP jsap = new SimpleJSAP(
                "QueryLucene",
                "Pulls information from Lucene",
                new Parameter[]{
                        new FlaggedOption("queryListFile", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 'q', "queryListFile",
                                "List of queries to run"),
                        new FlaggedOption("source", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 's', "querySources",
                                "Sources to query (source name, all, or aggregate)"),
                        new FlaggedOption("outputFile", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 'o', "outputFile",
                                "Path and name for output"),
                        new FlaggedOption("searchInterval", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 'i', "searchInterval",
                                "Search interval (monthly, daily, yearly, all"),
                        new FlaggedOption("startDate", JSAP.STRING_PARSER, "2000-01-01", JSAP.NOT_REQUIRED, 'b', "startDate",
                                "Start date"),
                        new FlaggedOption("endDate", JSAP.STRING_PARSER, "2007-05-31", JSAP.NOT_REQUIRED, 'f', "endDate",
                                "End date"),
                        new QualifiedSwitch("type", JSAP.STRING_PARSER, "count", JSAP.NOT_REQUIRED, 't', "type",
                                "Type of data output (queryCounts, dateRangeCounts, occurrenceList)").setList(true).setListSeparator(',')
                }
       );

        JSAPResult JSAPconfig = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);
        String type = JSAPconfig.getString("source");
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy/MM/dd");
        DateTime startDate = dateFormat.parseDateTime(JSAPconfig.getString("queryListFile"));
        DateTime endDate = dateFormat.parseDateTime(JSAPconfig.getString("queryListFile"));

        QueryLucene ql = new QueryLucene();
        CSVReader CSVReader = new CSVReader(new FileReader(JSAPconfig.getString("queryListFile")), '\t');
        List<String[]> queries = CSVReader.readAll();

        if ("queryCounts".equals(JSAPconfig.getString("type"))) {
            generateQueryCounts(startDate, endDate, JSAPconfig.getString("querySources"), ql, queries);
        } else if ("dateRangeCounts".equals(JSAPconfig.getString("type"))) {
            generateDateRangeCounts(startDate, endDate, JSAPconfig.getString("querySources"), ql, queries);
        } else if ("occurrenceList".equals(JSAPconfig.getString("type"))) {
            //know this is wrong, but whatevs
            for (String[] row : queries) {
                generateOccurenceList(startDate, endDate, row[0], JSAPconfig.getString("querySources"), ql);
            }
        } else {
            System.exit(1);
        }
        ql.saveFile(JSAPconfig.getString("outputFile"));
    }

    public static void generateOccurenceList(DateTime startDate, DateTime endDate, String querySources, String query, QueryLucene ql) throws IOException, ParseException {
        if (querySources.equals("all")) {
            /*   String[] searchFields = {"text", "mediaSource", "publicationDate"};
String source;
source = "New York Times";
executeOccurenceQuery(ql, new String[]{query, source, "[" + startDate.toString() + " to " + endDate.toString() + "]"}, searchFields);
source = "Los Angeles Times";
resultRow.add(ql.executeCountQuery(new String[]{column, source, "[" + startDate.toString() + " to " + endDate.toString() + "]"}, searchFields));
source = "Baltimore Sun";
resultRow.add(ql.executeCountQuery(new String[]{column, source, "[" + startDate.toString() + " to " + endDate.toString() + "]"}, searchFields));
source = "Chicago Tribune";
resultRow.add(ql.executeCountQuery(new String[]{column, source, "[" + startDate.toString() + " to " + endDate.toString() + "]"}, searchFields));*/
            System.out.println("Not supported");
            System.exit(1);
        } else if (querySources.equals("aggregate")) {
            String[] searchFields = {"text", "publicationDate"};
            executeOccurenceQuery(ql, new String[]{query, "*", "[" + startDate.toString() + " to " + endDate.toString() + "]"}, searchFields);
        } else {
            String[] searchFields = {"text", "mediaSource", "publicationDate"};
            executeOccurenceQuery(ql, new String[]{query, querySources, "[" + startDate.toString() + " to " + endDate.toString() + "]"}, searchFields);
        }
    }

    public static void executeOccurenceQuery(QueryLucene ql, String[] searchTerms, String[] searchFields) throws IOException, ParseException {
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
        Directory index = new SimpleFSDirectory(new File(LUCENE_INDEX_DIRECTORY));
        IndexReader reader = IndexReader.open(index);
        Query query = MultiFieldQueryParser.parse(Version.LUCENE_30, searchTerms, searchFields, analyzer);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(query, 1);
        Sort sort = new Sort(new SortField("publicationDate", SortField.INT));

        TopDocs hits = searcher.search(query, topDocs.totalHits, sort);

        int i = 0;
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            ArrayList<String> resultRow = new ArrayList<String>();
            Document doc = searcher.doc(scoreDoc.doc);
            resultRow.add(String.valueOf(i));
            resultRow.add(doc.get("publicationDate"));
            resultRow.add(doc.get("mediaSource"));
            resultRow.add(doc.get("filename"));
            resultRow.add(doc.get("headline"));

            ql.results.get().put(String.valueOf(i), resultRow);
            i++;
        }
    }

    public static void generateQueryCounts(DateTime startDate, DateTime endDate, String querySources, QueryLucene ql, List<String[]> queries) throws IOException, ParseException {
        for (String[] row : queries) {
            ArrayList<String> resultRow = new ArrayList<String>();
            String rowName = row[0];
            resultRow.add(rowName);
            for (String column : row) {
                if (querySources.equals("all")) {
                    String[] searchFields = {"text", "mediaSource", "publicationDate"};
                    String source;
                    source = "New York Times";
                    resultRow.add(ql.executeCountQuery(new String[]{column, source, "[" + startDate.toString() + " to " + endDate.toString() + "]"}, searchFields));
                    source = "Los Angeles Times";
                    resultRow.add(ql.executeCountQuery(new String[]{column, source, "[" + startDate.toString() + " to " + endDate.toString() + "]"}, searchFields));
                    source = "Baltimore Sun";
                    resultRow.add(ql.executeCountQuery(new String[]{column, source, "[" + startDate.toString() + " to " + endDate.toString() + "]"}, searchFields));
                    source = "Chicago Tribune";
                    resultRow.add(ql.executeCountQuery(new String[]{column, source, "[" + startDate.toString() + " to " + endDate.toString() + "]"}, searchFields));
                } else if (querySources.equals("aggregate")) {
                    String[] searchFields = {"text", "publicationDate"};
                    resultRow.add(ql.executeCountQuery(new String[]{column, "[" + startDate.toString() + " to " + endDate.toString() + "]"}, searchFields));
                } else {
                    String[] searchFields = {"text", "mediaSource", "publicationDate"};
                    resultRow.add(ql.executeCountQuery(new String[]{column, querySources, "[" + startDate.toString() + " to " + endDate.toString() + "]"}, searchFields));
                }
            }
            ql.results.get().put(rowName, resultRow);
        }
    }

    public static void generateDateRangeCounts(DateTime startDate, DateTime endDate, String querySources, QueryLucene ql, List<String[]> queries) throws IOException, ParseException {

        ql.results.get().put("", createHeader(startDate, endDate));
        for (String[] row : queries) {
            if (querySources.equals("all")) {
                issueDateRangeQueries(startDate, endDate, "*", row[0], ql);
            } else if (querySources.equals("aggregate")) {
                issueDateRangeQueries(startDate, endDate, "New York times", row[0], ql);
                issueDateRangeQueries(startDate, endDate, "Los Angeles Times", row[0], ql);
                issueDateRangeQueries(startDate, endDate, "Baltimore Sun", row[0], ql);
                issueDateRangeQueries(startDate, endDate, "Chicago Tribune", row[0], ql);
            } else {
                issueDateRangeQueries(startDate, endDate, querySources, row[0], ql);
            }
        }
    }

    private static void issueDateRangeQueries(DateTime startDate, DateTime endDate, String source, String queryText, QueryLucene ql) throws IOException, ParseException {
        ArrayList<String> resultRow = new ArrayList<String>();
        String rowName;
        String[] searchFields = {"text", "mediaSource", "publicationDate"};
        rowName = queryText + " " + source;
        for (DateTime date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            resultRow.add(ql.executeCountQuery(new String[]{queryText, source, "[" + startDate.toString() + " to " + endDate.toString() + "]"}, searchFields));
        }
        ql.results.get().put(rowName, resultRow);
    }
}
